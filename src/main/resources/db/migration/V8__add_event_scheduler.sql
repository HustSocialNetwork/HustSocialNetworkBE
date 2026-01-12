-- Enable the event scheduler
SET GLOBAL event_scheduler = ON;

-- Drop the event if it already exists to ensure we create the latest version
DROP EVENT IF EXISTS update_event_status;

-- Create the event to update statuses every minute
CREATE EVENT update_event_status
ON SCHEDULE EVERY 1 MINUTE
DO
BEGIN
    -- Update status to HAPPENING if start_time has passed, end_time hasn't, and it is currently UPCOMING
    UPDATE event
    SET status = 'HAPPENING'
    WHERE start_time <= NOW()
      AND end_time > NOW()
      AND status = 'UPCOMING';

    -- Update status to ENDED if end_time has passed
    UPDATE event
    SET status = 'ENDED'
    WHERE end_time <= NOW()
      AND status IN ('UPCOMING', 'HAPPENING');
END;
