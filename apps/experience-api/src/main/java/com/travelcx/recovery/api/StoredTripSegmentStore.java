package com.travelcx.recovery.api;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StoredTripSegmentStore {
    private final JdbcTemplate jdbcTemplate;

    public StoredTripSegmentStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StoredTripSegment> findByCaseId(String caseId) {
        return jdbcTemplate.query(
                "select case_id, segment_order, origin, destination, marketing_carrier, flight_number, scheduled_departure_at, scheduled_arrival_at from disruption_case_segment where case_id = ? order by segment_order",
                (rs, rowNum) -> new StoredTripSegment(
                        rs.getString("case_id"),
                        rs.getInt("segment_order"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("marketing_carrier"),
                        rs.getString("flight_number"),
                        rs.getObject("scheduled_departure_at", java.time.OffsetDateTime.class),
                        rs.getObject("scheduled_arrival_at", java.time.OffsetDateTime.class)),
                caseId);
    }

    public void replaceSegments(String caseId, List<StoredTripSegment> segments) {
        jdbcTemplate.update("delete from disruption_case_segment where case_id = ?", caseId);
        jdbcTemplate.batchUpdate(
                "insert into disruption_case_segment (case_id, segment_order, origin, destination, marketing_carrier, flight_number, scheduled_departure_at, scheduled_arrival_at) values (?, ?, ?, ?, ?, ?, ?, ?)",
                segments,
                segments.size(),
                (PreparedStatement ps, StoredTripSegment segment) -> {
                    ps.setString(1, segment.caseId());
                    ps.setInt(2, segment.segmentOrder());
                    ps.setString(3, segment.origin());
                    ps.setString(4, segment.destination());
                    ps.setString(5, segment.marketingCarrier());
                    ps.setString(6, segment.flightNumber());
                    ps.setObject(7, segment.scheduledDepartureAt());
                    ps.setObject(8, segment.scheduledArrivalAt());
                });
    }
}
