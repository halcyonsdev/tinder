CREATE TABLE IF NOT EXISTS swipes (
    id                     UUID PRIMARY KEY,
    first_swiper_id        UUID,
    second_swiper_id       UUID,
    first_swiper_decision  BOOLEAN,
    second_swiper_decision BOOLEAN
)