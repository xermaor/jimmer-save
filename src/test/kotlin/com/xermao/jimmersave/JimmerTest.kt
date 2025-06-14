package com.xermao.jimmersave

import com.xermao.jimmersave.entity.SteamBundle
import com.xermao.jimmersave.entity.addBy
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class JimmerTest(
) {
    @Autowired
    private lateinit var sqlClient: KSqlClient

    @Test
    fun test() {

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
}
