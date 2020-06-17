package com.example.shop.safika_health.Prevalent;

import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Model.Users;
import com.example.shop.safika_health.Model.healthProblem;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Prevalent {

    public  static Users currentOnlineUser;

    public  static Products detailProduct;
    public static List<healthProblem> problems = new ArrayList<healthProblem>();
    public static List<Products> all_products = new ArrayList<Products>();
    public static List<Products> recommended_products = new ArrayList<Products>();
    public static List<Products> favorited_products = new ArrayList<Products>();
    public static List<Products> cart_products = new ArrayList<Products>();

    public static int product_page_index = 0;

    public  static final String UserEmailKey = "UserEmail";
    public static final String UserPasswordkey = "UserPass";

}