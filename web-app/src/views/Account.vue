<template lang="html">
  <div class="account">
    <Nav account="true" :username="username"/>
    <div class="container">
      <div class="ui grid">
        <div class="four wide column">
          <div class="ui vertical fluid tabular menu">
            <a class="active item">
              History
            </a>
            <a class="item">
              ...
            </a>
            <a class="item">
              ...
            </a>
            <a class="item">
              ...
            </a>
          </div>
        </div>
        <div class="twelve wide stretched column">
          <div class="ui segment">
            <div class="history">
              <div class="ui items">
                <div class="item purchase" v-for="purchase in purchases" :key="purchase.id">
                  <div class="content">
                    <div class="header">
                      <h2 class="ui header">Purchase on {{ purchase.date }}</h2>
                    </div>
                    <div class="content">
                      <div class="ui divider"/>
                      <h3 class="ui header">Products</h3>
                      <table class="ui celled table">
                        <thead>
                          <tr>
                            <th>Name</th>
                            <th>Price</th>
                            <th>Amount</th>
                            <th>Total</th>
                          </tr></thead>
                          <tbody>
                            <tr class="product" v-for="product in purchase.products" :key="product.id">
                              <td data-label="Name">{{ product.name }}</td>
                              <td data-label="Age">{{ product.price }}</td>
                              <td data-label="Job">{{ product.amount}}</td>
                              <td data-label="Job">{{ product.price * product.amount }}</td>
                            </tr>
                          </tbody>
                          <tfoot>
                            <tr><th>TOTAL</th>
                              <th></th>
                              <th></th>
                              <th> {{ purchase.total }} </th>
                            </tr></tfoot>
                          </table>
                        </div>
                        <div class="description">
                          <p></p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
    </div>

  </div>
</template>

<script>
import Nav from '../components/Nav'
import cookie from '../cookies'
import axios from 'axios'

export default {
  components:{
    Nav,
  },
  data(){
    return {
      username:'',
      // Purchases viene de un request ajax
      purchases:[
        {
          id: 1,
          date:'2/10/2020',
          total: 40000,
          products:[
            {
              id: 1,
              image: 'image',
              name: 'Jabón',
              price: 8900,
              amount: 2,
            },
            {
              id: 2,
              image: 'image',
              name: 'Jabón',
              price: 8900,
              amount: 2,
            },
            {
              id: 3,
              image: 'image',
              name: 'Jabón',
              price: 8900,
              amount: 2,
            },
          ]
        },
      ]
    }
  },
  mounted(){
    this.products_in_cart = cookie.getCookie('products')
    var auth_token = cookie.getCookie('auth_token')
    if( typeof auth_token == "string" ){
      this.username = JSON.parse(auth_token).username;
    }
    const thisa = this;
    const url = "http://localhost:8080/buymicroservice-web-0.0.1-SNAPSHOT/GetCarts"
    const options = {
      method: 'POST',
      withCredentials: true,
      url,
    };
    axios(options).then(function (response) {
      console.log("Yes")
      console.log(response)
      thisa.purchases = response.data;
    })
    .catch(function (){
      thisa.errorLogin = true;
    })
  }
}
</script>

<style lang="css" scoped>
.account .container{
  padding-top: 5vh;
  padding-bottom: 10vh;
  padding-left: 5vh;
  padding-right: 5vh;
}
.history {
  padding: 5vh;
}
.history .purchase{
  margin-top: 10vh;
}
</style>
