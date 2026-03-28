package com.drivedash.admin.service;

import com.drivedash.admin.dto.DashboardStatsDto;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aggregates cross-module statistics for the admin dashboard using JPQL count
 * queries.  This service deliberately avoids importing entity classes from
 * other modules to keep the dependency graph clean; it queries by table name
 * via {@link EntityManager} native queries.
 *
 * <p>When later modules (UserManagement, TripManagement, etc.) are fully
 * implemented, counts can optionally be delegated to their service beans.
 * The dashboard controller uses this single service to avoid coupling
 * the controller to every module.
 */
@Service
@RequiredArgsConstructor
public class DashboardStatsService {

    private final EntityManager em;
    private final AdminNotificationService notificationService;

    @Transactional(readOnly = true)
    public DashboardStatsDto buildStats() {
        return DashboardStatsDto.builder()
                .totalCustomers(countWhere("users", "user_type = 'CUSTOMER' AND deleted_at IS NULL"))
                .totalDrivers(countWhere("users", "user_type = 'DRIVER' AND deleted_at IS NULL"))
                .totalTrips(countTable("trip_requests"))
                .completedTrips(countWhere("trip_requests", "current_status = 'completed'"))
                .pendingTrips(countWhere("trip_requests", "current_status = 'pending'"))
                .cancelledTrips(countWhere("trip_requests", "current_status = 'cancelled'"))
                .totalParcels(countTable("parcel_information"))
                .totalZones(countWhere("zones", "deleted_at IS NULL"))
                .activeVehicles(countWhere("vehicles", "is_active = 1 AND deleted_at IS NULL"))
                .totalTransactions(countTable("transactions"))
                .pendingWithdrawals(countWhere("withdraw_requests", "is_approved IS NULL"))
                .totalEarnings(sumColumn("transactions", "credit", null))
                .adminEarnings(sumColumn("trip_request_fees", "admin_commission", null))
                .unseenNotifications(notificationService.countUnseen())
                .build();
    }

    /** Monthly credit earnings for the current year — 12 entries (zero-filled). */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMonthlyEarnings() {
        List<Map<String, Object>> result = new ArrayList<>();
        // initialise all 12 months with 0
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                           "Jul","Aug","Sep","Oct","Nov","Dec"};
        for (int m = 1; m <= 12; m++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", months[m - 1]);
            entry.put("earnings", BigDecimal.ZERO);
            result.add(entry);
        }
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT MONTH(created_at) AS m, COALESCE(SUM(credit), 0) AS total " +
                    "FROM transactions " +
                    "WHERE YEAR(created_at) = YEAR(NOW()) " +
                    "GROUP BY MONTH(created_at)").getResultList();
            for (Object[] row : rows) {
                int monthIdx = ((Number) row[0]).intValue() - 1;
                BigDecimal total = (row[1] instanceof BigDecimal bd) ? bd
                        : BigDecimal.valueOf(((Number) row[1]).doubleValue());
                result.get(monthIdx).put("earnings", total);
            }
        } catch (Exception ignored) {}
        return result;
    }

    /** Top 5 drivers by completed trips count. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDriverLeaderboard() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT u.first_name, u.last_name, u.id, COUNT(t.id) AS cnt " +
                    "FROM trip_requests t " +
                    "JOIN users u ON u.id = t.driver_id " +
                    "WHERE t.current_status = 'completed' AND t.driver_id IS NOT NULL " +
                    "GROUP BY t.driver_id, u.first_name, u.last_name, u.id " +
                    "ORDER BY cnt DESC LIMIT 5").getResultList();
            for (Object[] row : rows) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("name", row[0] + " " + row[1]);
                e.put("userId", row[2]);
                e.put("trips", ((Number) row[3]).longValue());
                result.add(e);
            }
        } catch (Exception ignored) {}
        return result;
    }

    /** Top 5 customers by total trip count. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCustomerLeaderboard() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT u.first_name, u.last_name, u.id, COUNT(t.id) AS cnt " +
                    "FROM trip_requests t " +
                    "JOIN users u ON u.id = t.customer_id " +
                    "WHERE t.customer_id IS NOT NULL " +
                    "GROUP BY t.customer_id, u.first_name, u.last_name, u.id " +
                    "ORDER BY cnt DESC LIMIT 5").getResultList();
            for (Object[] row : rows) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("name", row[0] + " " + row[1]);
                e.put("userId", row[2]);
                e.put("trips", ((Number) row[3]).longValue());
                result.add(e);
            }
        } catch (Exception ignored) {}
        return result;
    }

    /** Last 10 trips for the recent-activity panel. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getRecentTrips() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT t.id, t.current_status, t.paid_fare, t.created_at, " +
                    "       uc.first_name AS cust_fn, uc.last_name AS cust_ln, " +
                    "       ud.first_name AS drv_fn, ud.last_name AS drv_ln " +
                    "FROM trip_requests t " +
                    "LEFT JOIN users uc ON uc.id = t.customer_id " +
                    "LEFT JOIN users ud ON ud.id = t.driver_id " +
                    "ORDER BY t.created_at DESC LIMIT 10").getResultList();
            for (Object[] row : rows) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("id",       row[0]);
                e.put("status",   row[1]);
                e.put("fare",     row[2]);
                e.put("date",     row[3] != null ? row[3].toString() : "");
                e.put("customer", row[4] + " " + row[5]);
                e.put("driver",   row[6] != null ? row[6] + " " + row[7] : "—");
                result.add(e);
            }
        } catch (Exception ignored) {}
        return result;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private long countTable(String table) {
        try {
            Object result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM " + table).getSingleResult();
            return toLong(result);
        } catch (Exception ex) {
            return 0L;
        }
    }

    private long countWhere(String table, String where) {
        try {
            Object result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM " + table + " WHERE " + where).getSingleResult();
            return toLong(result);
        } catch (Exception ex) {
            return 0L;
        }
    }

    private BigDecimal sumColumn(String table, String column, String where) {
        try {
            String sql = "SELECT COALESCE(SUM(" + column + "), 0) FROM " + table
                    + (where != null ? " WHERE " + where : "");
            Object result = em.createNativeQuery(sql).getSingleResult();
            if (result instanceof BigDecimal bd) return bd;
            if (result instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
            return BigDecimal.ZERO;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private long toLong(Object value) {
        if (value instanceof Number n) return n.longValue();
        return 0L;
    }
}
