CREATE TABLE IF NOT EXISTS ${flyway:defaultSchema}.reservations
(
    id                    uuid         PRIMARY KEY,
    spot_id               integer      NOT NULL,
    reservation_range     tstzrange    NOT NULL,
    user_id               uuid         NOT NULL,
    vehicle_licence_plate VARCHAR(255) NOT NULL,
    EXCLUDE USING GIST (spot_id WITH =, reservation_range WITH &&)
);
