package tech.iwish.taxi.other;

public class PickupLocationList {

    String description;
    String id;
    String matched_substrings;
    String place_id;
    String reference;
    String structured_formatting;
    String terms;

    public PickupLocationList(String description, String id, String matched_substrings, String place_id, String reference, String structured_formatting, String terms) {
        this.description = description;
        this.id = id;
        this.matched_substrings = matched_substrings;
        this.place_id = place_id;
        this.reference = reference;
        this.structured_formatting = structured_formatting;
        this.terms = terms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatched_substrings() {
        return matched_substrings;
    }

    public void setMatched_substrings(String matched_substrings) {
        this.matched_substrings = matched_substrings;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStructured_formatting() {
        return structured_formatting;
    }

    public void setStructured_formatting(String structured_formatting) {
        this.structured_formatting = structured_formatting;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }
}
