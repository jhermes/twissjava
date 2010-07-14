package example.models;

import java.util.List;

/**
 * A timeline is a paginated List of Tweets, with a Long representing the timestamp of the next tweet on the page.
 *  If nextview is null, then we've reached the end of the Timeline.
 */
public class Timeline {
    private List<Tweet> view;
    private Long nextview;

    public Timeline(List<Tweet> view, Long nextview) {
        this.view = view;
        this.nextview = nextview;
    }

    public List<Tweet> getView()
    {
        return view;
    }

    public Long getNextview()
    {
        return nextview;
    }

}