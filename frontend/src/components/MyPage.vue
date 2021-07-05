<template>

<v-data-table
    :headers="headers"
    :items="myPage"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'MyPage',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "reservationId", value: "reservationId" },
            { text: "itemId", value: "itemId" },
            { text: "qty", value: "qty" },
            { text: "reservationStatus", value: "reservationStatus" },
            { text: "startDate", value: "startDate" },
            { text: "endDate", value: "endDate" },
            { text: "name", value: "name" },
        ],
        myPage : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/mypages')

      this.myPage = temp.data._embedded.mypages;

    },
    methods: {
    }
  }
</script>

