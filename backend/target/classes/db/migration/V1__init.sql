-- Appointments table
CREATE TABLE appointments (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    title      VARCHAR(255) NOT NULL,
    date       DATE NOT NULL,
    start_time VARCHAR(5) NOT NULL,
    duration   INTEGER NOT NULL CHECK (duration IN (15, 30, 45, 60, 90, 120)),
    category   VARCHAR(20) NOT NULL CHECK (category IN ('WORK', 'PERSONAL', 'HEALTH', 'OTHER')),
    notes      TEXT
);

-- Tasks table
CREATE TABLE tasks (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed    BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    title        VARCHAR(255) NOT NULL,
    due_date     DATE,
    priority     VARCHAR(20) NOT NULL CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),
    notes        TEXT
);

-- Indexes for common query patterns
CREATE INDEX idx_appointments_date     ON appointments(date);
CREATE INDEX idx_tasks_priority        ON tasks(priority);
CREATE INDEX idx_tasks_completed       ON tasks(completed);
