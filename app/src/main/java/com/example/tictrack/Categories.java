package com.example.tictrack;

public class Categories {


        // variables for storing our data.
        private String categoryName, categoryDescription, categoryPicture;

        public Categories() {
            // empty constructor
            // required for Firebase.
        }

        // Constructor for all variables.
        public Categories(String categoryName, String categoryDescription, String categoryPicture) {
            this.categoryName = categoryName;
            this.categoryDescription = categoryDescription;
            this.categoryPicture = categoryPicture;
        }

        // getter methods for all variables.
        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryDescription() {
            return categoryDescription;
        }

        // setter method for all variables.
        public void setCategoryDescription(String categoryDescription) {
            this.categoryDescription = categoryDescription;
        }

        public String getCategoryPicture() {
            return categoryPicture;
        }

        public void setCategoryPicture(String categoryPicture) {
            this.categoryPicture = categoryPicture;
        }
    }


