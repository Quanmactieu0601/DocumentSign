UPDATE signature_template
SET activated = false
WHERE id in(    SELECT n1.id
                FROM signature_template n1, signature_template n2
                WHERE n1.user_id = n2.user_id AND n1.created_date < n2.created_date );
