package com.example.cs4125_project;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4125_project.enums.ProductType;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import com.example.cs4125_project.enums.Brand;
import com.example.cs4125_project.enums.ClothesStyles;
import com.example.cs4125_project.enums.Colour;
import com.example.cs4125_project.enums.ProductDatabaseFields;
import com.example.cs4125_project.enums.ProductType;
import com.example.cs4125_project.enums.Size;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProductInterfaceAdapter adapter;
    private FirebaseAuth mAuth;
    private Button logIn;
    private Button signOut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpRecyclerView();
        findViewById(R.id.logInBtn).setOnClickListener(this);
        findViewById(R.id.signOut).setOnClickListener(this);

        logIn = findViewById(R.id.logInBtn);
        signOut = findViewById(R.id.signOut);
        Database db = Database.getInstance();

        mAuth = FirebaseAuth.getInstance();
        isLoggedIn();

        String[]sizes = {Size.X_SMALL.getValue(),Size.SMALL.getValue(),Size.MEDIUM.getValue(), Size.LARGE.getValue(), Size.X_LARGE.getValue()};

        //pretend that user has clicked clothing tab
        ProductDatabaseController.setType(ProductType.CLOTHES);

        //Example usage of creating a product item from the product factory
        Map<String, Object> testClothes = new HashMap<>();
        testClothes.put(ProductDatabaseFields.NAME.getValue(), "Bumblebee Jumper");
        testClothes.put(ProductDatabaseFields.PRICE.getValue(), 69.99);
        testClothes.put(ProductDatabaseFields.SIZES.getValue(), Arrays.asList(sizes));
        testClothes.put(ProductDatabaseFields.QUANTITIES.getValue(), Arrays.asList(8,1,2,10,13));
        testClothes.put(ProductDatabaseFields.BRAND.getValue(), Brand.CALVINKLEIN.getValue());
        testClothes.put(ProductDatabaseFields.COLOUR.getValue(), Colour.YELLOW.getValue());
        testClothes.put(ProductDatabaseFields.STYLE.getValue(), ClothesStyles.JUMPER.getValue());
        //Generate product from product factory
        Product p = ProductFactory.getProduct(ProductType.CLOTHES, testClothes);
        //Uncomment when you want to actually add this item to the db
       // ProductDatabaseController.addProductToDB(p);

        //Example of querying filtered products
        Map<String, Object> testParams = new HashMap<>();
        testParams.put(ProductDatabaseFields.SIZES.getValue(), Size.X_LARGE.getValue());
        //testParams.put(ProductDatabaseFields.COLOUR.getValue(), Colour.BLUE.getValue());
        ProductDatabaseController.getFilteredProducts(testParams);

        //Example of updating a field in a product (grab an id from the db and put it in productId parameter)
        ProductDatabaseController.updateProductField("Rv8mIBM5sdYwvRYgouep ", ProductDatabaseFields.PRICE, 60.00);

        //Example of removing a product from a collection (grab an id from the db and put it in productId parameter)
        //ProductDatabaseController.removeProductFromDB("hottZJJwoB4hsKeGM0yC");

        //Gathers all the clothes items
        ProductDatabaseController.getProductCollection();
    }

    private void setUpRecyclerView() {
        adapter = new ProductInterfaceAdapter();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void goToLogIn(View v)
    {
        Fragment fr = new LogInFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content, fr);
        fragmentTransaction.addToBackStack("login");
        fragmentTransaction.commit();
    }

    private void isLoggedIn() {
        if(mAuth.getCurrentUser() != null) {
            logIn.setVisibility(View.INVISIBLE);
            signOut.setVisibility((View.VISIBLE));
        } else {
            logIn.setVisibility(View.VISIBLE);
            signOut.setVisibility((View.INVISIBLE));
        }
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.logInBtn) {
            goToLogIn(v);
        }
        if (i == R.id.signOut) {
            mAuth.signOut();
            isLoggedIn();
        }
    }
}
