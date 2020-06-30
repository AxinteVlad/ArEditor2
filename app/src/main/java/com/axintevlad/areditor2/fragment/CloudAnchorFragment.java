package com.axintevlad.areditor2.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.axintevlad.areditor2.R;
import com.axintevlad.areditor2.RecyclerItemClickListener;
import com.axintevlad.areditor2.activity.ARActivity;
import com.axintevlad.areditor2.adapter.ChosenFurnitureAdapter;
import com.axintevlad.areditor2.helpers.FirebaseManager;
import com.axintevlad.areditor2.helpers.ResolveDialogFragment;
import com.axintevlad.areditor2.helpers.StorageManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor.CloudAnchorState;
import com.axintevlad.areditor2.helpers.CloudAnchorManager;
import com.axintevlad.areditor2.helpers.SnackbarHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.core.Config;
import com.google.ar.core.Config.CloudAnchorMode;
import com.google.ar.core.Session;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Main Fragment for the Cloud Anchors Codelab.
 *
 * <p>This is where the AR Session and the Cloud Anchors are managed.
 */
public class CloudAnchorFragment extends ArFragment {
    private static final String TAG = "CloudAnchorFragment";
    private Scene arScene;
    private AnchorNode anchorNode;

    private final CloudAnchorManager cloudAnchorManager = new CloudAnchorManager();
    private final SnackbarHelper snackbarHelper = new SnackbarHelper();
    private Button resolveButton;
    private FloatingActionButton fabBtn;
    private ArrayList<String> imgIds;
    public FirebaseManager firebaseManager;
    private RecyclerView recyclerView;
    private ChosenFurnitureAdapter adapter;

    private boolean resolvePressed = false;
    private String modelName = null;
    private String modelNameFromDB = null;
    private ModelRenderable modelRenderable;
    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = super.getSessionConfiguration(session);
        config.setCloudAnchorMode(CloudAnchorMode.ENABLED);
        return config;
    }

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void onAttach(Context context) {
        super.onAttach(context);
        imgIds = getArguments().getStringArrayList("images");
        firebaseManager = new FirebaseManager(context);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate from the Layout XML file.
        View rootView = inflater.inflate(R.layout.cloud_anchor_fragment, container, false);
        LinearLayout arContainer = rootView.findViewById(R.id.ar_container);
        recyclerView = rootView.findViewById(R.id.ar_recyclerview);

        // Call the ArFragment's implementation to get the AR View.
        View arView = super.onCreateView(inflater, arContainer, savedInstanceState);
        arContainer.addView(arView);

        //Clear button
        Button clearButton = rootView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> onClearButtonPressed());

        //Resolve button
        resolveButton = rootView.findViewById(R.id.resolve_button);
        resolveButton.setOnClickListener(v -> onResolveButtonPressed());

        //Fab button
     /*   fabBtn = rootView.findViewById(R.id.fabButton);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        adapter = new ChosenFurnitureAdapter(getContext(), imgIds);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        drawObject(imgIds.get(position) + ".sfb");
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



        arScene = getArSceneView().getScene();
        arScene.addOnUpdateListener(frameTime -> cloudAnchorManager.onUpdate());
        setOnTapArPlaneListener((hitResult, plane, motionEvent) -> onArPlaneTap(hitResult));

        return rootView;
    }

    private synchronized void onArPlaneTap(HitResult hitResult) {
        if (anchorNode != null) {
            // Do nothing if there was already an anchor in the Scene.
            return;
        }
        Anchor anchor = hitResult.createAnchor();
        setNewAnchor(anchor);

        // The next line is the new addition.
        resolveButton.setEnabled(false);

        snackbarHelper.showMessage(getActivity(), "Now hosting anchor...");
        cloudAnchorManager.hostCloudAnchor(
                this.getArSceneView().getSession(), anchor, this::onHostedAnchorAvailable);
    }

    private synchronized void onClearButtonPressed() {
        // Clear the anchor from the scene.
        cloudAnchorManager.clearListeners();
        snackbarHelper.showMessage(getActivity(), "Anchor cleared");
        // The next line is the new addition.
        resolveButton.setEnabled(true);

        setNewAnchor(null);
    }

    private synchronized void onResolveButtonPressed() {
        ResolveDialogFragment dialog = ResolveDialogFragment.createWithOkListener(
                this::onShortCodeEntered);
        resolvePressed = true;
        dialog.show(this.getFragmentManager(), "Resolve");
    }

    // Modify the renderables when a new anchor is available.
    private synchronized void setNewAnchor(@Nullable Anchor anchor) {
        if (anchorNode != null) {
            // If an AnchorNode existed before, remove and nullify it.
            arScene.removeChild(anchorNode);
            anchorNode = null;
        }
        if (anchor != null) {


            // Create the Anchor.
            anchorNode = new AnchorNode(anchor);
            arScene.addChild(anchorNode);
            if(resolvePressed) {
                // Create the transformable andy and add it to the anchor.
//                TransformableNode andy = new TransformableNode(getTransformationSystem());
//                Log.d(TAG, "setNewAnchor: "+ modelNameFromDB);
//                ModelRenderable.builder()
//                        .setSource(getContext(), Uri.parse(modelNameFromDB))
//                        .build()
//                        .thenAccept(renderable -> modelRenderable = renderable);
//                andy.setParent(anchorNode);
//                andy.setRenderable(modelRenderable);
//                andy.select();
                ModelRenderable.builder()
                        .setSource(getContext(), Uri.parse(modelNameFromDB))
                        .build()
                        .thenAccept(modelRenderable -> {
                            modelName = Uri.parse(modelNameFromDB).toString();
                            addModelToScene(anchor, modelRenderable);})
                        .exceptionally(throwable -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(throwable.getMessage()).show();
                            return null;
                        });


            }

        }
    }
    private synchronized void onHostedAnchorAvailable(Anchor anchor) {
        CloudAnchorState cloudState = anchor.getCloudAnchorState();
        if (cloudState == CloudAnchorState.SUCCESS) {
            String cloudAnchorId = anchor.getCloudAnchorId();
            firebaseManager.nextShortCode(shortCode -> {
                if (shortCode != null) {
                    firebaseManager.storeUsingShortCode(shortCode, cloudAnchorId,modelName);

                    snackbarHelper
                            .showMessage(getActivity(), "Cloud Anchor Hosted. Short code: " + shortCode);
                } else {
                    // Firebase could not provide a short code.
                    snackbarHelper
                            .showMessage(getActivity(), "Cloud Anchor Hosted, but could not "
                                    + "get a short code from Firebase.");
                }
            });
            setNewAnchor(anchor);
        } else {
            snackbarHelper.showMessage(getActivity(), "Error while hosting: " + cloudState.toString());
        }
    }

    private synchronized void onShortCodeEntered(int shortCode) {
        firebaseManager.getModelName(shortCode, cloudAnchorId -> {
            if (cloudAnchorId == null || cloudAnchorId.isEmpty()) {
                snackbarHelper.showMessage(
                        getActivity(),
                        "A Cloud Anchor ID for the short code " + shortCode + " was not found.");
                return;
            }
            modelNameFromDB = cloudAnchorId;
        });

        firebaseManager.getCloudAnchorId(shortCode, cloudAnchorId -> {
            if (cloudAnchorId == null || cloudAnchorId.isEmpty()) {
                snackbarHelper.showMessage(
                        getActivity(),
                        "A Cloud Anchor ID for the short code " + shortCode + " was not found.");
                return;
            }
            resolveButton.setEnabled(false);
            Log.d(TAG, "onShortCodeEntered: " + getArSceneView().getSession());
            cloudAnchorManager.resolveCloudAnchor(
                    getArSceneView().getSession(),
                    cloudAnchorId,
                    anchor -> onResolvedAnchorAvailable(anchor, shortCode));
        });
    }

    private synchronized void onResolvedAnchorAvailable(Anchor anchor, int shortCode) {
        CloudAnchorState cloudState = anchor.getCloudAnchorState();
        if (cloudState == CloudAnchorState.SUCCESS) {
            snackbarHelper.showMessage(getActivity(), "Cloud Anchor Resolved. Short code: " + shortCode);
            setNewAnchor(anchor);
            //drawObject("bed1.obj");
        } else {
            snackbarHelper.showMessage(
                    getActivity(),
                    "Error while resolving anchor with short code "
                            + shortCode
                            + ". Error: "
                            + cloudState.toString());
            resolveButton.setEnabled(true);
        }
    }

    private void drawObject(String objectName) {
        this.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            // Fixed location & render overlay like marker
            Anchor anchor = hitResult.createAnchor();

            ModelRenderable.builder()
                    .setSource(getContext(), Uri.parse(objectName))
                    .build()
                    .thenAccept(modelRenderable -> {
                        modelName = Uri.parse(objectName).toString();
                        addModelToScene(anchor, modelRenderable);})
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(throwable.getMessage()).show();
                        return null;
                    });

            arScene = getArSceneView().getScene();
            arScene.addOnUpdateListener(frameTime -> cloudAnchorManager.onUpdate());
            onArPlaneTap(hitResult);
        });

    }
    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(this.getTransformationSystem());
        node.setRenderable(modelRenderable);
        node.setParent(anchorNode);
        this.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

}
