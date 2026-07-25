package com.fitmate.discovery.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Cached discovery feed wrapper. Deliberately a (non-final) class rather than a
 * record: the Redis JSON serializer uses NON_FINAL default typing to embed
 * {@code @class} type hints, and the typed {@code candidates} field lets Jackson
 * reconstruct the {@link CandidateCard} elements on cache reads.
 */
public class FeedResponse implements Serializable {

    private List<CandidateCard> candidates = new ArrayList<>();

    public FeedResponse() {
    }

    public FeedResponse(List<CandidateCard> candidates) {
        this.candidates = candidates;
    }

    public List<CandidateCard> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateCard> candidates) {
        this.candidates = candidates;
    }
}
