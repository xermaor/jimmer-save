package com.xermao.jimmersave.entity

import org.babyfish.jimmer.sql.meta.UserIdGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Matcher
import java.util.regex.Pattern

class SnowIdGenerator : UserIdGenerator<String> {
    @Synchronized
    fun snowflakeId(): String {
        return snowflake.nextStr()
    }


    override fun generate(entityType: Class<*>?): String {
        return snowflakeId()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SnowIdGenerator::class.java)
        private const val IPV4: String =
            "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$"
        private var WORKER_ID: Long = 0
        private var snowflake: SeataSnowflake

        init {
            WORKER_ID = getWorkerId()
            snowflake = SeataSnowflake(WORKER_ID)
            log.info("当前机器的 worker id: {}", WORKER_ID)
        }

        //-------------------------------------------------------------------------------- Private method start
        private fun getWorkerId(): Long {
            val compile = Pattern.compile(IPV4)
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val inetAddresses = networkInterfaces.nextElement().inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (inetAddress is Inet4Address
                        && inetAddress.isLoopbackAddress
                        && !inetAddress.isSiteLocalAddress
                        && !inetAddress.isLinkLocalAddress
                    ) {
                        try {
                            val matcher = compile.matcher(inetAddress.hostAddress)
                            return matchAddress(matcher)
                        } catch (_: Exception) {
                        }
                    }
                }
            }
            return 123456
        }


        private fun matchAddress(matcher: Matcher): Long {
            var addr = 0
            // 每个点分十进制数字 转为 8位二进制
            addr = addr or matcher.group(1).toInt()
            addr = addr shl 8
            addr = addr or matcher.group(2).toInt()
            addr = addr shl 8
            addr = addr or matcher.group(3).toInt()
            addr = addr shl 8
            addr = addr or matcher.group(4).toInt()

            // int的最高位无法直接使用，转为Long
            if (addr < 0) {
                return 0xffffffffL and addr.toLong()
            }
            return addr.toLong()
        }
    }

    private class SeataSnowflake(nodeId: Long) {
        private var nodeId: Long = 0
        private val timestampAndSequence: AtomicLong

        /**
         * 构造
         *
         * @param epochDate 初始化时间起点（null表示默认起始日期）,后期修改会导致id重复,如果要修改连workerId dataCenterId，慎用
         * @param nodeId    节点ID, 默认为DataCenterId或随机生成
         */
        init {
            val timestampWithSequence = (System.currentTimeMillis() - DEFAULT_TWEPOCH) shl SEQUENCE_BITS
            this.timestampAndSequence = AtomicLong(timestampWithSequence)
            initNodeId(nodeId)
        }

        /**
         * 获取下一个雪花ID
         *
         * @return id
         */
        fun next(): Long {
            val next = timestampAndSequence.incrementAndGet()
            val timestampWithSequence = next and timestampAndSequenceMask
            return nodeId or timestampWithSequence
        }

        /**
         * 下一个ID（字符串形式）
         *
         * @return ID 字符串形式
         */
        fun nextStr(): String {
            return next().toString()
        }

        /**
         * 初始化节点ID
         *
         * @param nodeId 节点ID
         */
        private fun initNodeId(nodeId: Long) {
            var newNodeId = nodeId
            if (nodeId > MAX_NODE_ID || nodeId < 0) {
                newNodeId = nodeId.coerceAtLeast(0) % MAX_NODE_ID
            }
            this.nodeId = newNodeId shl (TIMESTAMP_BITS + SEQUENCE_BITS)
        }

        companion object {
            /**
             * 默认的起始时间，为2020-05-03
             */
            const val DEFAULT_TWEPOCH: Long = 1588435200000L

            // 节点ID长度
            private const val NODE_ID_BITS = 10

            /**
             * 节点ID的最大值，1023
             */
            protected const val MAX_NODE_ID: Int = (-1 shl NODE_ID_BITS).inv()

            // 时间戳长度
            private const val TIMESTAMP_BITS = 41

            // 序列号12位（表示只允许序号的范围为：0-4095）
            private const val SEQUENCE_BITS = 12

            // 时间戳+序号的最大值
            private const val timestampAndSequenceMask = (-1L shl (TIMESTAMP_BITS + SEQUENCE_BITS)).inv()
        }
    }
}
