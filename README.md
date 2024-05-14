<h1>PromoCode application</h1>

<h3>To run this application:</h3>
<p>Go to the main project directory and use maven wrapper to create jar file:</p>
<p><i><strong>.\mvnw install</strong></i></p>
<p>then go to the target directory and run:</p>
<p><i><strong>java -jar your-app-name.jar</strong></i></p>
<p>to start the application.</p>
<p>You can also go to the main project class which is <strong>ApiApplication.java</strong> in src/main/java/com/promocodes/api/ directory and start application manually using your IDE interface.</p>
<br/>

<h3>API endpoints:</h3>
<p>To get API documentation, you can open: <strong>http://localhost:8080/swagger-ui/index.html</strong> and test it using Swagger or you can use any API client and use URL's given below.</p>
<h3>PRODUCT</h3>

<h5>Add new product:</h5>
<p><strong>POST | http://localhost:8080/api/products</strong><br/>
sample body data:</p>
<pre>
{
  "name": "Water",
  "description": "Water description", // optional
  "price": "3.75",
  "currency": "PLN"
}
</pre>

<hr>
<h5>Get all products:</h5>
<p><strong>GET | http://localhost:8080/api/products</strong><br/>

<hr>
<h5>Update product data:</h5>
<p><strong>PUT | http://localhost:8080/api/products/{product_id}</strong><br/>
sample body data:</p>
<pre>
{
  "name": " New water",
  "description": "New water description", // optional
  "price": "3.25",
  "currency": "PLN"
}
</pre>

<hr>
<h5>Get product discount price:</h5>
<p><strong>GET | http://localhost:8080/api/products/{product_id}?code={promo_code}</strong><br/>
<br/>
<hr>


<h3>PROMO CODE</h3>
<h5>Add new promo code:</h5>
<p><strong>POST | http://localhost:8080/api/codes</strong><br/>
sample body data:</p>
<pre>
{
  "code": "Summer2024",
  "expireDate": "2024-05-18",
  "maxUsages": 15,
  "amount": "20.50",
  "currency": "PLN",
  "codeType": "QUANTITATIVE"  // QUANTITATIVE | PERCENTAGE
}
</pre>
<hr>
<h5>Get specific promo code:</h5>
<p><strong>GET | http://localhost:8080/api/codes/{code}</strong><br/>

<hr>
<h5>Get all promo codes:</h5>
<p><strong>GET | http://localhost:8080/api/codes</strong><br/>
<br/>
<hr>

<h3>PURCHASE</h3>
<h5>Make new purchase:</h5>
<p><strong>POST | http://localhost:8080/api/purchases?productId={product_id}&code={promo_code}</strong><br/>
<hr>

<h5>Get sales report:</h5>
<p><strong>GET | http://localhost:8080/api/purchases/report</strong><br/>







