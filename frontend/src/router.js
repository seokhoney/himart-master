
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ItemManager from "./components/ItemManager"


import MyPage from "./components/MyPage"
import StoreManager from "./components/StoreManager"

import ReservationManager from "./components/ReservationManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/Item',
                name: 'ItemManager',
                component: ItemManager
            },


            {
                path: '/MyPage',
                name: 'MyPage',
                component: MyPage
            },
            {
                path: '/Store',
                name: 'StoreManager',
                component: StoreManager
            },

            {
                path: '/Reservation',
                name: 'ReservationManager',
                component: ReservationManager
            },



    ]
})
