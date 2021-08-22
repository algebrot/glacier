package com.example.glacier;

import java.util.Map;
import java.util.Set;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private Callout mCallout;
    private ServiceFeatureTable table0, table1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // authentication with an API key or named user is required to access basemaps and other
        // location services
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);

        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView);
        // create an ArcGISMap with a topographic basemap
        final ArcGISMap map = new ArcGISMap(Basemap.Type.LIGHT_GRAY_CANVAS_VECTOR, 48.6596, -113.7870, 9);
        // set the ArcGISMap to the MapView
        mMapView.setMap(map);
        // get the callout that shows attributes
        mCallout = mMapView.getCallout();

        // layer 0, facilities
        table0 = new ServiceFeatureTable(getResources().getString(R.string.layer0_url));
        final FeatureLayer featureLayer0 = new FeatureLayer(table0);

        // layer 1, trails
        table1 = new ServiceFeatureTable(getResources().getString(R.string.layer1_url));
        final FeatureLayer featureLayer1 = new FeatureLayer(table1);

        map.getOperationalLayers().add(featureLayer0);
        map.getOperationalLayers().add(featureLayer1);

        // set an on touch listener to listen for click events
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // remove any existing callouts
                if (mCallout.isShowing()) {
                    mCallout.dismiss();
                }
                // get the point that was clicked and convert it to a point in map coordinates
                final Point screenPoint = new Point(Math.round(e.getX()), Math.round(e.getY()));
                // create a selection tolerance
                int tolerance = 10;
                // use identifyLayerAsync to get tapped features
                final ListenableFuture<IdentifyLayerResult> identifyLayerResultListenableFuture0 = mMapView
                        .identifyLayerAsync(featureLayer0, screenPoint, tolerance, false, 1);
                identifyLayerResultListenableFuture0.addDoneListener(() -> {
                    try {
                        IdentifyLayerResult identifyLayerResult = identifyLayerResultListenableFuture0.get();
                        // create a textview to display field values
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine(false);
                        calloutContent.setVerticalScrollBarEnabled(true);
                        calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                        calloutContent.setMovementMethod(new ScrollingMovementMethod());
                        calloutContent.setLines(5);
                        for (GeoElement element : identifyLayerResult.getElements()) {
                            Feature feature = (Feature) element;
                            // create a map of all available attributes as name value pairs
                            Map<String, Object> attr = feature.getAttributes();
                            Set<String> keys = attr.keySet();
                            for (String key : keys) {
                                if (key.contains("GlobalID")) {
                                    keys.remove(key);
                                }
                            }
                            for (String key : keys) {
                                Object value = attr.get(key);
                                calloutContent.append(key + " | " + value + "\n");
                            }
                            // center the mapview on selected feature
                            Envelope envelope = feature.getGeometry().getExtent();
                            mMapView.setViewpointGeometryAsync(envelope, 200);
                            // show callout
                            mCallout.setLocation(envelope.getCenter());
                            mCallout.setContent(calloutContent);
                            mCallout.show();
                        }
                    } catch (Exception e1) {
                        Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e1.getMessage());
                    }
                });
                // use identifyLayerAsync to get tapped features
                final ListenableFuture<IdentifyLayerResult> identifyLayerResultListenableFuture1 = mMapView
                        .identifyLayerAsync(featureLayer1, screenPoint, tolerance, false, 1);
                identifyLayerResultListenableFuture1.addDoneListener(() -> {
                    try {
                        IdentifyLayerResult identifyLayerResult = identifyLayerResultListenableFuture1.get();
                        // create a textview to display field values
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine(false);
                        calloutContent.setVerticalScrollBarEnabled(true);
                        calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                        calloutContent.setMovementMethod(new ScrollingMovementMethod());
                        calloutContent.setLines(5);
                        for (GeoElement element : identifyLayerResult.getElements()) {
                            Feature feature = (Feature) element;
                            // create a map of all available attributes as name value pairs
                            Map<String, Object> attr = feature.getAttributes();
                            Set<String> keys = attr.keySet();
                            for (String key : keys) {
                                if (key.contains("GlobalID") | key.contains("OBJECTID")) {
                                    keys.remove(key);
                                }
                            }
                            for (String key : keys) {
                                Object value = attr.get(key);
                                    calloutContent.append(key + " | " + value + "\n");
                            }
                            // center the mapview on selected feature
                            Envelope envelope = feature.getGeometry().getExtent();
                            mMapView.setViewpointGeometryAsync(envelope, 200);
                            // show callout
                            mCallout.setLocation(envelope.getCenter());
                            mCallout.setContent(calloutContent);
                            mCallout.show();
                        }
                    } catch (Exception e1) {
                        Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e1.getMessage());
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}