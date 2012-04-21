(let [oid (ObjectId.)]
  (insert "documents" { :_id oid :first_name "John" :last_name "Lennon" })
  (find-map-by-id oid))
