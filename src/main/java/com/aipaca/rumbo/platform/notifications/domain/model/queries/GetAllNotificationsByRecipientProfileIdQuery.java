package com.aipaca.rumbo.platform.notifications.domain.model.queries;

/**
 * Get All Notifications By Recipient Profile ID Query
 *
 * @param recipientProfileId recipient profile id
 */
public record GetAllNotificationsByRecipientProfileIdQuery(Long recipientProfileId) {
    public GetAllNotificationsByRecipientProfileIdQuery {
        if (recipientProfileId == null || recipientProfileId < 1) {
            throw new IllegalArgumentException("Recipient profile id cannot be null or less than 1");
        }
    }
}
