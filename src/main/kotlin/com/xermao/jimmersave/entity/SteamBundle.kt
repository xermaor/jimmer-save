package com.xermao.jimmersave.entity

import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "steam_bundle_test")
interface SteamBundle : IdEntity {

    @Key
    @Column(name = "bundle_id")
    val bundleId: Int

    @ManyToMany
    @JoinTable(name = "steam_bundle_app_map_test", joinColumnName = "bundle_id", inverseJoinColumnName = "app_id")
    val apps: List<SteamGame>
}
