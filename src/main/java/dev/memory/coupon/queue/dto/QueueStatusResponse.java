package dev.memory.coupon.queue.dto;

public record QueueStatusResponse(
        Long userId,
        Long rank,
        Boolean canIssue
) {
    public static QueueStatusResponse of(Long userId, Long rank, Boolean canIssue) {
        return new QueueStatusResponse(userId, rank, canIssue);
    }

}
