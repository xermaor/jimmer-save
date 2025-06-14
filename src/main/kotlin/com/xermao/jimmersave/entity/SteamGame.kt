package com.xermao.jimmersave.entity

import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "steam_game_test")
interface SteamGame : IdEntity {

    @Key
    @Column(name = "appid")
    val appid: Int

    @ManyToMany
    val bundles: List<SteamBundle>
}
