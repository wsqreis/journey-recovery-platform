package com.travelcx.recovery.worker;

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
                        "select case_id, booking_reference, disruption_type, detected_at, impacted_passengers, delay_minutes, connection_at_risk, overnight_impact, high_value_itinerary, customer_id, customer_full_name, loyalty_tier, traveling_with_children, requires_accessibility_support, vip_customer, corporate_traveler, recommendation_action, recommendation_score, recommendation_summary, recommendation_explanation, recommendation_priority, recommendation_sla_bucket, recommendation_human_review_required, recommendation_premium_customer, recommendation_reasons, updated_at from disruption_case where case_id = ?",
                        (rs, rowNum) -> new StoredDisruptionCase(
                                rs.getString("case_id"),
                                rs.getString("booking_reference"),
                                rs.getString("disruption_type"),
                                rs.getObject("detected_at", java.time.OffsetDateTime.class),
                                rs.getInt("impacted_passengers"),
                                rs.getInt("delay_minutes"),
                                rs.getBoolean("connection_at_risk"),
                                rs.getBoolean("overnight_impact"),
                                rs.getBoolean("high_value_itinerary"),
                                rs.getString("customer_id"),
                                rs.getString("customer_full_name"),
                                rs.getString("loyalty_tier"),
                                rs.getBoolean("traveling_with_children"),
                                rs.getBoolean("requires_accessibility_support"),
                                rs.getBoolean("vip_customer"),
                                rs.getBoolean("corporate_traveler"),
                                rs.getString("recommendation_action"),
                                rs.getInt("recommendation_score"),
                                rs.getString("recommendation_summary"),
                                rs.getString("recommendation_explanation"),
                                rs.getString("recommendation_priority"),
                                rs.getString("recommendation_sla_bucket"),
                                rs.getBoolean("recommendation_human_review_required"),
                                rs.getBoolean("recommendation_premium_customer"),
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
                "insert into disruption_case (case_id, booking_reference, disruption_type, detected_at, impacted_passengers, delay_minutes, connection_at_risk, overnight_impact, high_value_itinerary, customer_id, customer_full_name, loyalty_tier, traveling_with_children, requires_accessibility_support, vip_customer, corporate_traveler, recommendation_action, recommendation_score, recommendation_summary, recommendation_explanation, recommendation_priority, recommendation_sla_bucket, recommendation_human_review_required, recommendation_premium_customer, recommendation_reasons, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict (case_id) do update set booking_reference = excluded.booking_reference, disruption_type = excluded.disruption_type, detected_at = excluded.detected_at, impacted_passengers = excluded.impacted_passengers, delay_minutes = excluded.delay_minutes, connection_at_risk = excluded.connection_at_risk, overnight_impact = excluded.overnight_impact, high_value_itinerary = excluded.high_value_itinerary, customer_id = excluded.customer_id, customer_full_name = excluded.customer_full_name, loyalty_tier = excluded.loyalty_tier, traveling_with_children = excluded.traveling_with_children, requires_accessibility_support = excluded.requires_accessibility_support, vip_customer = excluded.vip_customer, corporate_traveler = excluded.corporate_traveler, recommendation_action = excluded.recommendation_action, recommendation_score = excluded.recommendation_score, recommendation_summary = excluded.recommendation_summary, recommendation_explanation = excluded.recommendation_explanation, recommendation_priority = excluded.recommendation_priority, recommendation_sla_bucket = excluded.recommendation_sla_bucket, recommendation_human_review_required = excluded.recommendation_human_review_required, recommendation_premium_customer = excluded.recommendation_premium_customer, recommendation_reasons = excluded.recommendation_reasons, updated_at = excluded.updated_at",
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
        ps.setBoolean(9, storedDisruptionCase.highValueItinerary());
        ps.setString(10, storedDisruptionCase.customerId());
        ps.setString(11, storedDisruptionCase.customerFullName());
        ps.setString(12, storedDisruptionCase.loyaltyTier());
        ps.setBoolean(13, storedDisruptionCase.travelingWithChildren());
        ps.setBoolean(14, storedDisruptionCase.requiresAccessibilitySupport());
        ps.setBoolean(15, storedDisruptionCase.vipCustomer());
        ps.setBoolean(16, storedDisruptionCase.corporateTraveler());
        ps.setString(17, storedDisruptionCase.recommendationAction());
        ps.setInt(18, storedDisruptionCase.recommendationScore());
        ps.setString(19, storedDisruptionCase.recommendationSummary());
        ps.setString(20, storedDisruptionCase.recommendationExplanation());
        ps.setString(21, storedDisruptionCase.recommendationPriority());
        ps.setString(22, storedDisruptionCase.recommendationSlaBucket());
        ps.setBoolean(23, storedDisruptionCase.recommendationHumanReviewRequired());
        ps.setBoolean(24, storedDisruptionCase.recommendationPremiumCustomer());
        ps.setString(25, storedDisruptionCase.recommendationReasons());
        ps.setObject(26, storedDisruptionCase.updatedAt());
    }
}
