/**
 * Module descriptor for the Courier Common Library.
 *
 * <p>This module contains the shared DTOs ({@code com.courier.common.dto})
 * and the RMI remote interface ({@code com.courier.common.remote}) used by
 * both the server and client modules.</p>
 *
 * <p>Both packages are exported so they are accessible to {@code courier.server}
 * and {@code courier.client}.</p>
 */
module courier.common {

    // ── Java RMI for the Remote interface ───────────────────────────────
    requires java.rmi;

    // ── Export the shared packages ──────────────────────────────────────
    exports com.courier.common.dto;
    exports com.courier.common.remote;
}
