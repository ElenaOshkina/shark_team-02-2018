package park.sharkteam.views.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Alex on 02.04.2018.
 */
public class ScoreForm {
    private int startPosition;

    private int elementsLimit;


    public ScoreForm(
            @JsonProperty("startPos") int startPosition,
            @JsonProperty("numberElements") int elementsLimit
    ) {
        this.startPosition = startPosition;
        this.elementsLimit = elementsLimit;
    }

    public int getStartPosition() {
        return this.startPosition;
    }
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getElementsLimit() {
        return this.elementsLimit;
    }
    public void setElementsLimit(int elementsLimit) {
        this.elementsLimit = elementsLimit;
    }
}
