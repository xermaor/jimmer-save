package com.xermao.jimmersave

import com.xermao.jimmersave.entity.SteamBundle
import com.xermao.jimmersave.entity.addBy
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
class JimmerTest(
) {
    @Autowired
    private lateinit var sqlClient: KSqlClient

    /**
     * Fixed in jimmer version 0.9.93
     */
    @Test
    @Sql("/database.sql")
    fun test1() {

        val steamBundle = SteamBundle {
            bundleId = 1
            apps().addBy {
                this.appid = 1
            }
            apps().addBy {
                this.appid = 2
            }
        }

        sqlClient.save(steamBundle) {
            setAssociatedMode(SteamBundle::apps, AssociatedSaveMode.APPEND_IF_ABSENT)
        }
    }

    @Test
    @Transactional(rollbackFor = [Exception::class])
    @Sql("/database.sql")
    fun test2() {
        val steamBundles = listOf(
            SteamBundle {
                bundleId = 1
                apps().addBy {
                    this.appid = 2
                }
            },
            SteamBundle {
                bundleId = 2
                apps().addBy {
                    this.appid = 2
                }
            }
        )


        sqlClient.saveEntities(steamBundles) {
            setAssociatedMode(SteamBundle::apps, AssociatedSaveMode.APPEND_IF_ABSENT)
        }
    }
}
