package com.nohjunh.test.model.event

data class SteamDataEvent(
  val data: String,
  val type: Int = TYPE_STEAM
) {
  companion object {
    const val TYPE_STEAM = 0
    const val TYPE_STEAM_START = 1
    const val TYPE_STEAM_END = 2
  }

  fun isSteamStart() = type == TYPE_STEAM_START

  fun isSteamEnd() = type == TYPE_STEAM_END

  fun isSteam() = type == TYPE_STEAM
}