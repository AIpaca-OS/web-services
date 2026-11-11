package com.aipaca.rumbo.routes.domain.model;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
@Embeddable
public class RouteStop {
    @Column(name="stop_id", nullable=false) private UUID id;
    @Column(name="address", nullable=false, length=350) private String address;
    @Column(name="position", nullable=false) private int position;
    protected RouteStop() {}
    public RouteStop(UUID id, String address, int position) {
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Stop address is required");
        this.id = id == null ? UUID.randomUUID() : id;
        this.address = address.trim(); this.position = position;
    }
    public UUID getId() { return id; }
    public String getAddress() { return address; }
    public int getPosition() { return position; }
}
