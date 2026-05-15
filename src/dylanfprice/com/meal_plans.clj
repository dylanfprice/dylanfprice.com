(ns dylanfprice.com.meal-plans)

(def meal-plans
  {"Week 1"
   [{:main [:a {:href "/recipes/baked-potato"} "Baked Potato"]
     :side "Salad"}
    {:main [:a {:href "/recipes/meatballs"} "Meatballs"]
     :side "Spaghetti and Sauce"}
    {:main [:a {:href "/recipes/mushroom-goat-cheese-quesadillas"} "Mushroom and Goat Cheese Quesadillas"]
     :side "Tortillas"}
    {:main [:a {:href "/recipes/green-curry-chickpeas"} "Green Curry Chickpeas"]
     :side "Rice"}
    {:main [:a {:href "/recipes/risotto"} "Risotto"]
     :side "Sunflower seeds"}
    {:main [:a {:href "/recipes/calzones"} "Calzones"]
     :side "Salad"}
    {:main "Burgers"
     :side "Sweet Potato Wedges"}]
   "Week 2"
   [{:main [:a {:href "/recipes/teriyaki-tempeh"} "Teriyaki Tempeh"]
     :side "Rice"}
    {:main [:a {:href "/recipes/korean-bbq-tofu-bowl"} "Korean BBQ Tofu Bowl"]
     :side "Rice or Quinoa"}
    {:main [:a {:href "/recipes/jerk-kidney-beans"} "Jerk Kidney Beans"]
     :side [:span "Nachos or " [:a {:href "/recipes/classic-cornbread"} "Classic Cornbread"]]}
    {:main [:a {:href "/recipes/southern-fried-tempeh"} "Southern Fried Tempeh"]
     :side [:span
            [:a {:href "/recipes/smashed-potatoes"} "Smashed Potatoes"]
            " and Salad"]}
    {:main [:a {:href "/recipes/tofu-enchiladas"} "Enchiladas"]
     :side [:span
            [:a {:href "/recipes/red-enchilada-sauce"} "Red Sauce"]
            " or "
            [:a {:href "/recipes/green-enchilada-sauce"} "Green Sauce"]]}
    {:main "Sausage and Brussel Sprouts"
     :side "Boiled Potatoes"}]
   "Week 3"
   [{:main [:a {:href "/recipes/nacho-ranch-chickpea-bowl"} "Nacho Ranch Chickpea Bowl"]}
    {:main "?"
     :side [:a {:href "/recipes/bombay-potatoes"} "Bombay Potatoes"]}
    {:main [:a {:href "/recipes/baked-feta-pasta"} "Baked Feta Pasta"]}
    {:main [:a {:href "/recipes/peanut-sauce-fried-rice"} "Peanut-Sauce Fried Rice"]}
    {:main [:a {:href "/recipes/vegan-spinach-alfredo-lasagna"} "Spinach Alfredo Lasagna"]}
    {:main [:a {:href "/recipes/tomato-pesto-tart"} "Tomato Pesto Tart"]
     :side "Salad"}
    {:main [:a {:href "/recipes/berbere-tofu-bowl"} "Berbere Tofu Bowl"]}]})
