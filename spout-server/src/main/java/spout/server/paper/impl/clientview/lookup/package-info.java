/**
 * <h1>Client view - Lookup</h1>
 *
 * <p>
 * Provides the {@link spout.server.paper.impl.clientview.lookup.ClientViewLookup}
 * interface for any class that can look up its associated client view,
 * and implements that interface for:
 * <ul>
 *     <li>{@link net.minecraft.network.Connection}</li>
 *     <li>{@link net.minecraft.server.network.ServerLoginPacketListenerImpl}</li>
 *     <li>{@link net.minecraft.server.network.ServerCommonPacketListenerImpl}</li>
 *     <li>{@link net.minecraft.server.level.ServerPlayer}</li>
 * </ul>
 * </p>
 */
@NullMarked
package spout.server.paper.impl.clientview.lookup;

import org.jspecify.annotations.NullMarked;
