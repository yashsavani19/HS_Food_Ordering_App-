package com.example.mrgstuckshopapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrgstuckshopapp.Adaptor.FoodAdaptor;
import com.example.mrgstuckshopapp.Model.FoodModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CartFragment extends Fragment  implements FoodAdaptor.GetOneFood {

    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FoodAdaptor mAdaptor;
    double orderPrice=0;
    FloatingActionButton confirmorder;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);

    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        List<FoodModel> foodModels = new ArrayList<>();
//        FoodModel foodModel =new FoodModel();
//        foodModel.setFoodid("1");
//        foodModel.setDescription("Hello");
//        foodModel.setPrice(Integer.parseInt("1"));
//        foodModel.setImageURL("https://raw.githubusercontent.com/17527/MRGS_Tuckshop_/master/app/src/main/res/drawable-v24/wedges.jpg");
//        foodModel.setFoodname("hello");
//        foodModel.setQuantity(Integer.parseInt("1"));
//        foodModels.add(foodModel);
        confirmorder = view.findViewById(R.id.confirm_button);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recViewAll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdaptor = new FoodAdaptor(this);
        getCartOrder();





    }

   void getCartOrder()
    {

        List<FoodModel> foodModels = new ArrayList<>();

        firebaseFirestore.collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {


                if (task.isSuccessful()){

                    for (DocumentSnapshot ds: Objects.requireNonNull(task.getResult()).getDocuments()){
                        FoodModel foodModel =new FoodModel();
                        foodModel.setFoodid(ds.getId());
                        double totalPrice = Integer.parseInt(ds.get("price").toString()) * Integer.parseInt(ds.get("quantity").toString());
                        orderPrice = orderPrice + totalPrice;
                        foodModel.setDescription("Quantity : "+Integer.parseInt(ds.get("quantity").toString()) + " Price : "+totalPrice +"0");
                        foodModel.setImageURL(ds.get("imageURL").toString());
                        foodModel.setFoodname(ds.get("foodname").toString());
                        foodModels.add(foodModel);



                    }
                    mAdaptor.setFoodModelList(foodModels);
                    recyclerView.setAdapter(mAdaptor);
                    Toast.makeText(getActivity(), ""+orderPrice, Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("FinalCartFetchException",e.toString());
            }
        });

    }

    @Override
    public void clickedFood(int position, List<FoodModel> foodModels) {

    }

}