# **PIZZERIA REST API Documentation**

Linket til vores API: https://pizza.jenjoelle.dk/api/routes

Formålet med vores API endpoints er at skabe en lille illusion om et website med en webshop, der skal fungere som en pizza butik. Vi vil derfor gerne kunne indlæse en pizza liste med alle pizzaer. og foretage crud operationer for rollerne:

Som Kunde / User vil det omhandle bl.a  (Se pizzaer, Køb pizza, Slet pizza fra indkøbskurv).

Som Sælgeren / Admin kan det omhandle ordreoversigt, ( Lav pizza, Se pizza liste, Slette pizza, Redigere / Opdatere pizza \- og ved nogle af disse operationer også ordne flere pizzaer på en gang) Nu får vi se hvor meget vi når\!



| Method | URL | Request Body (JSON) | Response (JSON) | Error (e) |
| :---- | :---- | :---- | :---- | :---- |
| **GET** | /api/routes |  |  |  |
| **GET**    	 | /api/users |   | \[user, user, ...\](1) |  404 not found (hvis der ikke er nogen users forbundet til API’et) |
| **GET**    	 | /api/users/{id}        	 |   | user (1) | 404 not found (hvis useren ikke findes)  |
| **POST** | /api/user | user(1) without id |  | Fejl når vi sender request med forkert request format, fejl når useren allerede findes |
| **POST** | /api/users | \[user1,user2, ...\](1)without id |   | Fejl når vi sender request med forkert request format, fejl når useren allerede findes |
| **UPDATE** | /api/users/{id} | user (1) | user (1) |  404 not found (hvis useren ikke findes), fejl hvis formatet på request er forkert |
| **DELETE** | /api/users/{id} |  | User \[user (1)\] has been deleted | 404 not found (hvis useren ikke findes)  |
| **POST** | /api/pizza | pizza (1) without id |  | Fejl når vi sender request med forkert request format, fejl når pizzaen allerede findes |
| **POST** | /api/pizzas | \[pizza1,pizza2, ...\](1) without id |  | Fejl når vi sender request med forkert request format, fejl når pizzaen allerede findes |
| **GET** | /api/pizza/{id} |  | pizza (1) | 404 not found (hvis pizzaen ikke findes) |
| **GET** | /api/pizzas |  | \[pizza1, pizza2, ...\](1) | 404 not found (hvis der ikke er pizzaer på menuen/i listen) |
| **DELETE** | /api/pizza/{id} |  | Pizza \[pizza (1)\] has been deleted | 404 not found (hvis pizzaen ikke findes) |
| **UPDATE** | /api/pizza/{id} | pizza (1) | pizza (1) | 404 not found (hvis pizzaen ikke findes), fejl hvis formatet på request er forkert |
| **UPDATE** | /api/pizzas | \[pizza1,pizza2, ...\](1) | \[pizza1,pizza2, ...\](1) | 404 not found (hvis pizzaerne ikke findes), fejl hvis formatet på request er forkert |



#### **Request Body and Response Formats**



(1) User format (don’t provide ID, for POST)

**User:**

{

	"id": Number,  
 	"age": Number,  
 	"name": String,  
 	"gender": String \[“Male” | “Female” | “Other”\],  
 	"email": String (email)  
}

**Pizza:**

{

	"id": Number,  
 	"name": String,  
 	"toppings”: String,  
 	"price”: Number  
}



**Order:**

{

	"order\_id": Number,

 	"order\_date": Number,

 	"total\_price": Number,

 	"user\_id": Number,      // Foreign key to User

	"order\_item\_list": \[

		{

			"order\_item\_id": Number,

			"pizza\_id": Number,  // Foreign key to Pizza

			"quantity": Number,

			"pizza\_price": Number

		},

		{

			"order\_item\_id": Number,

			"pizza\_id": Number,  // Foreign key to Pizza

			"quantity": Number,

			"pizza\_price": Number

		}

	\]

}

#### **Errors**

(e) All errors are reported using this format (with the HTTP-status code matching the number)

{ status : statusCode, "msg": "Explains the problem" }



●       (e1) : { status : 404, "msg": "No content found for this request" }

●       (e2) : { status : 400, "msg": "Field ‘xxx’ is required" } (for example, no name provided)

●       (e3) : { status : 401, "msg": "No user is logged in" } (for example, no user is currently logged in)

●       (e4) : { status : 403, "msg": "Current user does not have access rights to this content" } (for example, a customer wants to delete a pizza of the pizzerias menu \- which is only reserved for the administrators)


Jaja Ding Dong 🎶 - [Eurovision Song Contest: The Story of Fire Saga](https://www.youtube.com/watch?v=PlBUH8zMZng&pp=ygUOamFqYSBkaW5nIGRvbmc%3D)