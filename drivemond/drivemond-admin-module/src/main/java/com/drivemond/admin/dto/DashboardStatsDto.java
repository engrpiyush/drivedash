package com.drivemond.admin.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

/**
 * Aggregated statistics rendered on the admin dashboard.
 * Fields are populated by {@link com.drivemond.admin.service.DashboardStatsService}.
 * Counts from other modules are injected via their service beans.
 */
@Getter
@Builder
public class DashboardStatsDto {

    private long totalCustomers;
    private long totalDrivers;
    private long totalTrips;
    private long completedTrips;
    private long pendingTrips;
    private long cancelledTrips;
    private long totalParcels;
    private long totalZones;
    private long activeVehicles;
    private long totalTransactions;
    private long pendingWithdrawals;

    private BigDecimal totalEarnings;
    private BigDecimal adminEarnings;

    private long unseenNotifications;
}
