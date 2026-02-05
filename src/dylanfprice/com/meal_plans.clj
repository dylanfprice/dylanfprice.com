(ns dylanfprice.com.meal-plans)

(def meal-plans
  {"Week 1"
   [{:entree [:a {:href "/recipes/baked-potato"} "Baked Potato"]
     :side "Salad"}
    {:entree [:a {:href "/recipes/meatballs"} "Meatballs"]
     :side "Spaghetti and Sauce"}
    {:entree [:a {:href "/recipes/mushroom-goat-cheese-quesadillas"} "Mushroom and Goat Cheese Quesadillas"]
     :side "Tortillas"}
    {:entree [:a {:href "/recipes/green-curry-chickpeas"} "Green Curry Chickpeas"]
     :side "Rice"}
    {:entree [:a {:href "/recipes/risotto"} "Risotto"]
     :side "Sunflower seeds"}
    {:entree [:a {:href "/recipes/calzones"} "Calzones"]
     :side "Salad"}
    {:entree "Burgers"
     :side "Sweet Potato Wedges"}]
   "Week 2"
   [{:entree [:a {:href "/recipes/teriyaki-tempeh"} "Teriyaki Tempeh"]
     :side "Rice"}
    {:entree [:a {:href "/recipes/korean-bbq-tofu-bowl"} "Korean BBQ Tofu Bowl"]
     :side "Rice or Quinoa"}
    {:entree [:a {:href "/recipes/jerk-kidney-beans"} "Jerk Kidney Beans"]
     :side [:span "Nachos or " [:a {:href "/recipes/classic-cornbread"} "Classic Cornbread"]]}
    {:entree [:a {:href "/recipes/tempeh-ratatouille"} "Tempeh Ratatouille"]
     :side "Pasta"}
    {:entree [:a {:href "/recipes/tofu-enchiladas"} "Enchiladas"]
     :side [:span
            [:a {:href "/recipes/red-enchilada-sauce"} "Red Sauce"]
            " or "
            [:a {:href "/recipes/green-enchilada-sauce"} "Green Sauce"]]}
    {:entree "Sausage and Brussel Sprouts"
     :side "Boiled Potatoes"}]
   "Week 3"
   [{:entree [:a {:href "/recipes/nacho-ranch-chickpea-bowl"} "Nacho Ranch Chickpea Bowl"]}
    {:entree "?"
     :side [:a {:href "/recipes/bombay-potatoes"} "Bombay Potatoes"]}
    {:entree [:a {:href "/recipes/baked-feta-pasta"} "Baked Feta Pasta"]}
    {:entree [:a {:href "/recipes/peanut-sauce-fried-rice"} "Peanut-Sauce Fried Rice"]}
    {:entree [:a {:href "/recipes/vegan-spinach-alfredo-lasagna"} "Spinach Alfredo Lasagna"]}
    {:entree [:a {:href "/recipes/tomato-pesto-tart"} "Tomato Pesto Tart"]
     :side "Salad"}
    {:entree [:a {:href "/recipes/berbere-tofu-bowl"} "Berbere Tofu Bowl"]}]})
