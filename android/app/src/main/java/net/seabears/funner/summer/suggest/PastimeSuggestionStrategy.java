package net.seabears.funner.summer.suggest;

import android.content.Context;

public interface PastimeSuggestionStrategy {
    String getQuery(Context context);

    String[] getArguments(SuggestArgs args);
}
