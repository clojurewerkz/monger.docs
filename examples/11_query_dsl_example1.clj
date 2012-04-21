(with-collection "movies"
  (find { :year { $lt 2010 $gte 2000 }, :revenue { $gt 20000000 } })  
  (fields [ :year :title :producer :cast :budget :revenue ])
  (sort-by { :revenue -1 })
  (skip 10)
  (limit 20)
  (hint "year-by-year-revenue-idx")
  (snapshot))