package com.example.mrgstuckshopapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.mrgstuckshopapp.Model.FoodModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;


public class FoodDescriptionFragment extends Fragment {

    NavController navController;
    int quantity = 0;
    FirebaseFirestore firebaseFirestore;
    Button add, sub, order;
    TextView foodname, description, quantityview, orderINFO;
    ImageView imageView;
    String foodid;
    String name;
    String fooddescription;
    String imageURL;
    int price = 0;
    int totalPrice = 0;

    public FoodDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_desrciption, container, false);
    }

    //setting the values for each of the ids
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.fooddetailimage);
        foodname = view.findViewById(R.id.fooddetailname);
        description = view.findViewById(R.id.fooddetaildetail);
        add = view.findViewById(R.id.upquantity);
        sub = view.findViewById(R.id.downquantity);
        quantityview = view.findViewById(R.id.fooddetailquantity);
        firebaseFirestore = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(view);
        order = view.findViewById(R.id.addtocartorderdetail);
        orderINFO = view.findViewById(R.id.orderINFO);


        name = FoodDescriptionFragmentArgs.fromBundle(getArguments()).getFoodname();
        foodid = FoodDescriptionFragmentArgs.fromBundle(getArguments()).getId();
        imageURL = FoodDescriptionFragmentArgs.fromBundle(getArguments()).getImageURL();
        price = FoodDescriptionFragmentArgs.fromBundle(getArguments()).getPrice();
        fooddescription = FoodDescriptionFragmentArgs.fromBundle(getArguments()).getDescription();
        quantity = FoodDescriptionFragmentArgs.fromBundle(getArguments()).getQuantity();

        //glide is an external dependency which helps load the image URL
        Glide.with(view.getContext()).load(imageURL).into(imageView);

        //this sets the food name with the price and also sets the description
        foodname.setText(name + " $" + String.valueOf(price));
        description.setText(fooddescription);

        //fetching the recent quantity from firestore and displaying it
        firebaseFirestore.collection("Food").document(foodid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot ds, FirebaseFirestoreException error) {

                fetchCurrentCartQuantity();
                quantityview.setText(String.valueOf(quantity));

                totalPrice = Integer.parseInt(ds.get("price").toString()) * quantity;
                orderINFO.setText("$ " + String.valueOf(totalPrice));
            }
        });


// add button increases the quantity
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quantity == 10) {
                    Toast.makeText(getContext(), "Nothing in Cart", Toast.LENGTH_SHORT).show();
                    quantityview.setText(String.valueOf(quantity));

                } else {

                    quantity++;
                    quantityview.setText(String.valueOf(quantity));

                    //showing the price of the increased quantity
                    totalPrice = quantity * price;
                    orderINFO.setText(String.valueOf("$ " + totalPrice));


                }


            }
        });
// sub button decreases the quantity
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quantity == 0) {
                    Toast.makeText(getContext(), "Nothing in Cart", Toast.LENGTH_SHORT).show();
                    quantityview.setText(String.valueOf(quantity));

                } else {

                    quantity--;
                    quantityview.setText(String.valueOf(quantity));

                    //showing the price
                    totalPrice = quantity * price;
                    orderINFO.setText(String.valueOf("$ " + totalPrice));


                }


            }
        });



        //calls the AddtoCart void when add to cart button is clicked
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddtoCart();
            }
        });
    }

    void fetchCurrentCartQuantity() {
        firebaseFirestore.collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {


                if (task.isSuccessful()) {


                    for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult()).getDocuments()) {

                        if (ds.get("foodname").toString().contains(name)) {
                            quantity = Integer.parseInt(ds.get("quantity").toString());
                            quantityview.setText(String.valueOf(quantity));

                            totalPrice = Integer.parseInt(ds.get("price").toString()) * quantity;
                            orderINFO.setText("$ " + String.valueOf(totalPrice));

                        }

                    }

                }


            }
        });
    }

    public String cartfoodid() {
        firebaseFirestore.collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {


                for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult()).getDocuments()) {
                    ds.get(cartfoodid());
                }
            }

        });
        return cartfoodid();
    }

    private void AddtoCart() {


            FoodModel foodModel = new FoodModel();
            foodModel.setFoodid(foodid);
            foodModel.setDescription(fooddescription);
            foodModel.setImageURL(imageURL);
            foodModel.setQuantity(quantity);
            foodModel.setFoodname(name);
            foodModel.setPrice(price);


// Check food item is present in cart or not
// If food item is already in cart then just update the quantity
            firebaseFirestore.collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {


                    if (task.isSuccessful()) {
                        boolean flag = false;
                        String id = "";

                        for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult()).getDocuments()) {

                            if (ds.get("foodname").toString().contains(name)) {
                                flag = true;
                                id = ds.getId();

                            }

                        }
                        if (flag) {
                            if (quantity > 0) {
                                firebaseFirestore.collection("Cart").document(id).update("quantity", quantity).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        Toast.makeText(getContext(), "Order Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("AddtoCartException", "" + e);
                                    }
                                });
                            }
                            if (quantity == 0) {
                                firebaseFirestore.collection("Cart").document(id).delete();
                                navController.navigate(R.id.action_foodDescriptionFragment_to_foodlistfragment);
                                Toast.makeText(getContext(), "You did not order " + name, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if(quantity>0)
                            {
                                firebaseFirestore.collection("Cart").add(foodModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("AddtoCartException", "" + e);
                                    }
                                });
                            }
                            if(quantity==0)
                            {
                                navController.navigate(R.id.action_foodDescriptionFragment_to_foodlistfragment);
                                Toast.makeText(getContext(), "You did not order " + name, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }


                }
            });





    }
}
// To delete all documents from Cart
//firebaseFirestore.collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//@Override
//public void onComplete(Task<QuerySnapshot> task) {
//
//
//        if (task.isSuccessful()){
//
//        for (DocumentSnapshot ds: Objects.requireNonNull(task.getResult()).getDocuments()){
//        ds.getReference().delete();
//
//
//
//        }
//        }
//
//
//        }
//        });