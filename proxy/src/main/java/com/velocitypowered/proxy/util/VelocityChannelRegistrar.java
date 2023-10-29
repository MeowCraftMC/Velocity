/*
 * Copyright (C) 2018-2023 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.velocitypowered.proxy.util;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import com.velocitypowered.proxy.network.protocol.util.PluginMessageUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Registry for channels recognized by the proxy.
 */
public class VelocityChannelRegistrar implements ChannelRegistrar {

  private final Map<String, ChannelIdentifier> identifierMap = new ConcurrentHashMap<>();

  private static boolean isModernIdentifier(ChannelIdentifier identifier) {
    return identifier.id().indexOf(':') != -1;
  }

  @Override
  public void register(ChannelIdentifier... identifiers) {
    for (ChannelIdentifier identifier : identifiers) {
      if (isModernIdentifier(identifier)) {
        identifierMap.put(identifier.id(), identifier);
      } else {
        String rewritten = PluginMessageUtil.transformLegacyToModernChannel(identifier.id());
        identifierMap.put(identifier.id(), identifier);
        identifierMap.put(rewritten, identifier);
      }
    }
  }

  @Override
  public void unregister(ChannelIdentifier... identifiers) {
    for (ChannelIdentifier identifier : identifiers) {
      if (isModernIdentifier(identifier)) {
        identifierMap.remove(identifier.id());
      } else {
        String rewritten = PluginMessageUtil.transformLegacyToModernChannel(identifier.id());
        identifierMap.remove(identifier.id());
        identifierMap.remove(rewritten);
      }
    }
  }

  /**
   * Returns all legacy channel IDs.
   *
   * @return all legacy channel IDs
   */
  public Collection<String> getLegacyChannelIds() {
    Collection<String> ids = new HashSet<>();
    for (ChannelIdentifier value : identifierMap.values()) {
      ids.add(value.id());
    }
    return ids;
  }

  /**
   * Returns all channel IDs (as strings) for use with Minecraft 1.13 and above.
   *
   * @return the channel IDs for Minecraft 1.13 and above
   */
  public Collection<String> getModernChannelIds() {
    Collection<String> ids = new HashSet<>();
    for (ChannelIdentifier value : identifierMap.values()) {
      if (isModernIdentifier(value)) {
        ids.add(value.id());
      } else {
        ids.add(PluginMessageUtil.transformLegacyToModernChannel(value.id()));
      }
    }
    return ids;
  }

  public @Nullable ChannelIdentifier getFromId(String id) {
    return identifierMap.get(id);
  }

  /**
   * Returns all the channel names to register depending on the Minecraft protocol version.
   *
   * @param protocolVersion the protocol version in use
   * @return the list of channels to register
   */
  public Collection<String> getChannelsForProtocol(ProtocolVersion protocolVersion) {
    if (protocolVersion.compareTo(ProtocolVersion.MINECRAFT_1_13) >= 0) {
      return getModernChannelIds();
    }
    return getLegacyChannelIds();
  }
}
