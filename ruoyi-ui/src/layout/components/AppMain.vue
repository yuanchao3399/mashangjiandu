<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view :key="key" />
      </keep-alive>
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  data () {
    return {
      socket: "",
    }
  },
  computed: {
    cachedViews () {
      return this.$store.state.tagsView.cachedViews
    },
    key () {
      return this.$route.path
    }
  },
  mounted () {
    // 初始化
    this.init()
  },
  methods: {
    init: function () {
      if (typeof (WebSocket) === "undefined") {
        alert("您的浏览器不支持socket")
      } else {
        // 实例化socket
        // 
        this.socket = new WebSocket(`ws://localhost:40000/websocket/${this.$store.getters.deptId}`)
        // 42.228.16.37
        // 监听socket连接
        this.socket.onopen = this.open
        // 监听socket错误信息
        this.socket.onerror = this.error
        // 监听socket消息
        this.socket.onmessage = this.getMessage
      }
    },
    open: function () {
      console.log("连接成功")
    },
    error: function () {
      console.log("连接错误")
    },
    getMessage: function (msg) {
      let _this = this;
      this.$notify.info({
        dangerouslyUseHTMLString: true,
        title: '新消息提示',
        message: `<p style="cursor:pointer">${msg.data}</p>`,
        onClick () {
          _this.toPage(msg.data)
        }
      });
    },
    send: function () {
      // this.socket.send(params)
    },
    close: function () {
      console.log("已经关闭")
    },
    toPage (data) {
      let reg = RegExp(/投诉/)
      let isTS = reg.test(data)
      isTS ? this.$router.push({ path: '/report/report' }) : this.$router.push({ path: '/report/complaints' })
    }
  },
  destroyed () {
    // 销毁监听
    this.socket.onclose = this.close
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  /* 50= navbar  50  */
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 17px;
  }
}
</style>
