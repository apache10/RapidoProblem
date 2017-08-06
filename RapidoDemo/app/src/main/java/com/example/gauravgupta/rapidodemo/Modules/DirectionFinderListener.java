package com.example.gauravgupta.rapidodemo.Modules;

/**
 * Created by gauravgupta on 06/08/17.
 */

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
