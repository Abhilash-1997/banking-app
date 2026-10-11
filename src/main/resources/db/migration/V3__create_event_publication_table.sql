-- ================================================================
-- V3 — Create Spring Modulith Event Publication Registry Table
-- ================================================================
--
-- Spring Modulith's Event Publication Registry implements the
-- Transactional Outbox pattern at the application level.
--
-- When an application event (e.g., CustomerRegisteredEvent) is published
-- inside a @Transactional method:
-- 1. Spring Modulith writes an uncompleted entry into this table
--    as part of the SAME database transaction.
-- 2. When the @ApplicationModuleListener finishes executing successfully,
--    the entry's completion_date is updated.
-- 3. If the application crashes before the listener completes,
--    uncompleted events can be republished on next startup.
--
-- This guarantees at-least-once event delivery across modules without
-- needing an external message broker like Kafka for in-process events!
-- ================================================================

CREATE TABLE IF NOT EXISTS event_publication
(
    id               UUID                     NOT NULL,
    listener_id      VARCHAR(512)             NOT NULL,
    event_type       VARCHAR(512)             NOT NULL,
    serialized_event TEXT                     NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date  TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_event_publication PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_event_publication_completion_date
    ON event_publication (completion_date);
