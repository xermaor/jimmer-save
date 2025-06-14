package com.xermao.jimmersave.entity

import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IdEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(generatorType = SnowIdGenerator::class)
    val id: String
}
