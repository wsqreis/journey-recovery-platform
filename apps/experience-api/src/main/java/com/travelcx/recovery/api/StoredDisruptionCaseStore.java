package com.travelcx.recovery.api;

import java.sql.PreparedStatement;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StoredDisruptionCaseStore {
    private final JdbcTemplate jdbcTemplate;

    public StoredDisruptionCaseStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<StoredDisruptionCase> findById(String caseId) {
        return jdbcTemplate
                .query(
                        "select case_id, booking_reference, disruption_type, detected_at, impacted_passengers, delay_minutes, connection_at_risk, overnight_impact, customer_id, customer_full_name, loyalty_tier, traveling_with_children, requires_accessibility_support, recommendation_action, recommendation_score, recommendation_summary, recommendation_explanation, recommendation_reasons, updated_at from disruption_case where case_id = ?",
                        (rs, rowNum) -> new StoredDisruptionCase(
                                rs.getString("case_id"),
                                rs.getString("booking_reference"),
                                rs.getString("disruption_type"),
                                rs.getObject("detected_at", java.time.OffsetDateTime.class),
                                rs.getInt("impacted_passengers"),
                                rs.getInt("delay_minutes"),
                                rs.getBoolean("connection_at_risk"),
                                rs.getBoolean("overnight_impact"),
                                rs.getString("customer_id"),
                                rs.getString("customer_full_name"),
                                rs.getString("loyalty_tier"),
                                rs.getBoolean("traveling_with_children"),
                                rs.getBoolean("requires_accessibility_support"),
                                rs.getString("recommendation_action"),
                                rs.getInt("recommendation_score"),
                                rs.getString("recommendation_summary"),
                                rs.getString("recommendation_explanation"),
                                rs.getString("recommendation_reasons"),
                                rs.getObject("updated_at", java.time.OffsetDateTime.class)),
                        caseId)
                .stream()
                .findFirst();
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from disruption_case");
    }

    public void upsert(StoredDisruptionCase storedDisruptionCase) {
        jdbcTemplate.update(
                "insert into disruption_case (case_id, booking_reference, disruption_type, detected_at, impacted_passengers, delay_minutes, connection_at_risk, overnight_impact, customer_id, customer_full_name, loyalty_tier, traveling_with_children, requires_accessibility_support, recommendation_action, recommendation_score, recommendation_summary, recommendation_explanation, recommendation_reasons, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict (case_id) do update set booking_reference = excluded.booking_reference, disruption_type = excluded.disruption_type, detected_at = excluded.detected_at, impacted_passengers = excluded.impacted_passengers, delay_minutes = excluded.delay_minutes, connection_at_risk = excluded.connection_at_risk, overnight_impact = excluded.overnight_impact, customer_id = excluded.customer_id, customer_full_name = excluded.customer_full_name, loyalty_tier = excluded.loyalty_tier, traveling_with_children = excluded.traveling_with_children, requires_accessibility_support = excluded.requires_accessibility_support, recommendation_action = excluded.recommendation_action, recommendation_score = excluded.recommendation_score, recommendation_summary = excluded.recommendation_summary, recommendation_explanation = excluded.recommendation_explanation, recommendation_reasons = excluded.recommendation_reasons, updated_at = excluded.updated_at",
                ps -> bindStoredDisruptionCase(ps, storedDisruptionCase));
    }

    private void bindStoredDisruptionCase(PreparedStatement ps, StoredDisruptionCase storedDisruptionCase)
            throws java.sql.SQLException {
        ps.setString(1, storedDisruptionCase.caseId());
        ps.setString(2, storedDisruptionCase.bookingReference());
        ps.setString(3, storedDisruptionCase.disruptionType());
        ps.setObject(4, storedDisruptionCase.detectedAt());
        ps.setInt(5, storedDisruptionCase.impactedPassengers());
        ps.setInt(6, storedDisruptionCase.delayMinutes());
        ps.setBoolean(7, storedDisruptionCase.connectionAtRisk());
        ps.setBoolean(8, storedDisruptionCase.overnightImpact());
        ps.setString(9, storedDisruptionCase.customerId());
        ps.setString(10, storedDisruptionCase.customerFullName());
        ps.setString(11, storedDisruptionCase.loyaltyTier());
        ps.setBoolean(12, storedDisruptionCase.travelingWithChildren());
        ps.setBoolean(13, storedDisruptionCase.requiresAccessibilitySupport());
        ps.setString(14, storedDisruptionCase.recommendationAction());
        ps.setInt(15, storedDisruptionCase.recommendationScore());
        ps.setString(16, storedDisruptionCase.recommendationSummary());
        ps.setString(17, storedDisruptionCase.recommendationExplanation());
        ps.setString(18, storedDisruptionCase.recommendationReasons());
        ps.setObject(19, storedDisruptionCase.updatedAt());
    }
}
