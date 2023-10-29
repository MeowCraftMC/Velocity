/*
 * Copyright (C) 2018-2023 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package com.velocitypowered.api.event.player;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.event.annotation.AwaitingEvent;
import com.velocitypowered.api.network.connection.InboundConnection;
import com.velocitypowered.api.util.GameProfile;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This event is fired after the {@link com.velocitypowered.api.event.connection.PreLoginEvent} in
 * order to set up the game profile for the user. This can be used to configure a custom profile for
 * a user, i.e. skin replacement.
 *
 * <p>
 * Velocity will wait for this event to finish firing before proceeding with the rest of the login
 * process, but you should try to limit the work done in any event that fires during the login
 * process.
 * </p>
 */
@AwaitingEvent
public final class GameProfileRequestEvent {

  private final String username;
  private final InboundConnection connection;
  private final GameProfile originalProfile;
  private final boolean onlineMode;
  private @Nullable GameProfile gameProfile;

  /**
   * Creates a new instance.
   *
   * @param connection      the connection connecting to the proxy
   * @param originalProfile the original {@link GameProfile} for the user
   * @param onlineMode      whether or not the user connected in online or offline mode
   */
  public GameProfileRequestEvent(InboundConnection connection, GameProfile originalProfile,
      boolean onlineMode) {
    this.connection = Preconditions.checkNotNull(connection, "connection");
    this.originalProfile = Preconditions.checkNotNull(originalProfile, "originalProfile");
    this.username = originalProfile.name();
    this.onlineMode = onlineMode;
  }

  public InboundConnection connection() {
    return connection;
  }

  public String username() {
    return username;
  }

  public GameProfile originalProfile() {
    return originalProfile;
  }

  public boolean onlineMode() {
    return onlineMode;
  }

  /**
   * Returns the game profile that will be used to initialize the connection with. Should no profile
   * be currently specified, the one generated by the proxy (for offline mode) or retrieved from the
   * Mojang session servers (for online mode) will be returned instead.
   *
   * @return the user's {@link GameProfile}
   */
  public GameProfile profileToUse() {
    return gameProfile == null ? originalProfile : gameProfile;
  }

  /**
   * Sets the game profile to use for this connection.
   *
   * @param gameProfile the profile for this connection, {@code null} uses the original profile
   */
  public void setProfileToUse(@Nullable GameProfile gameProfile) {
    this.gameProfile = gameProfile;
  }

  @Override
  public String toString() {
    return "GameProfileRequestEvent{"
        + "username=" + username
        + ", gameProfile=" + gameProfile
        + "}";
  }


}
