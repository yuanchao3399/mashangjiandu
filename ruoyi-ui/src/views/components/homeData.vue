<template>
  <div class="app_container">
    <el-container>
      <el-main>
        <el-row class="top_card">
          <el-col :span="6" v-for="(item,index) in mainCardData" :key="item.text+index">
            <div class="box_card">
              <div class="card_title">{{item.text}}</div>
              <div class="card_content">
                <div class="left_box">
                  <p class="text">投诉</p>
                  <p>{{item.leftNum}}</p>
                </div>
                <div class="right_box" v-if="$store.getters.roles != 'zwbjgc'">
                  <p class=" text">举报</p>
                  <p>{{item.rightNum}}</p>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="main_echarts">
          <el-card>
            <div id="echarts1" class="echarts_box"></div>
          </el-card>
          <el-card v-if="$store.getters.roles != 'zwbjgc'">
            <div id="echarts2" class="echarts_box"></div>
          </el-card>
          <el-card>
            <div id="echarts3" class="echarts_box"></div>
          </el-card>
          <el-card v-if="$store.getters.roles != 'zwbjgc'">
            <div id="echarts4" class="echarts_box"></div>
          </el-card>
        </div>
      </el-main>
      <el-aside width="360px">
        <el-button v-if="$store.getters.roles != 'zwbjgc'" @click="exportFile" type="warning" icon="el-icon-download" size="small" class="yellow_btn">导出</el-button>
        <div class="select_date">
          <el-date-picker v-model="dateTime" @change="searchInfo" type="daterange" size="small" unlink-panels range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" :picker-options="pickerOptions" />
        </div>
        <div class="aside_card">
          <div class="box_card" v-for="(item,index) in asideCardData" :key="item.text+index+item.leftNum">
            <div class="card_title">{{item.text}}</div>
            <div class="card_content">
              <div class="num">{{item.num}}</div>
            </div>
          </div>
        </div>
        <div class="aside_echarts">
          <el-card style="margin-bottom: 16px;">
            <div id="echarts5" class="echarts_box"></div>
          </el-card>
          <el-card v-if="$store.getters.roles != 'zwbjgc'">
            <div id="echarts6" class="echarts_box"></div>
          </el-card>
        </div>
      </el-aside>
    </el-container>
  </div>
</template>

<script>
import { getHomeNumAll, getHomePieChart, getHomePieChartByTime, getHomeLineChart, getHomeBarChart, } from '../../api/index'
export default {
  name: "homeDatas",
  data () {
    return {
      data: {
        jubao: {
          'shebao': 10,
          'gongjijin': 20,
          'jinrong': 30,
        },
        tousu: {
          'shebao': 10,
          'gongjijin': 20,
          'jinrong': 30,
        },
      },
      mainCardData: [
        { text: '年度总量', leftNum: '', rightNum: '' },
        { text: '本月总量', leftNum: '', rightNum: '' },
        { text: '全日量', leftNum: '', rightNum: '' },
        { text: '待处理', leftNum: '', rightNum: '' }
      ],
      dateTime: ['', ''],
      pickerOptions: {
        shortcuts: [{
          text: '本月',
          onClick (picker) {
            const start = new Date();
            start.setDate(1);
            picker.$emit('pick', [start, new Date()]);
          }
        }, {
          text: '今年至今',
          onClick (picker) {
            const end = new Date();
            const start = new Date(new Date().getFullYear(), 0);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近六个月',
          onClick (picker) {
            const end = new Date();
            const start = new Date();
            start.setMonth(start.getMonth() - 6);
            picker.$emit('pick', [start, end]);
          }
        }],
        disabledDate (time) {
          return time.getTime(new Date()) > Date.now();
        },
      },
      asideCardData: [
        { text: '投诉', num: '' },
        { text: '举报', num: '' },
      ],
      echarts1Info: [],
      echarts2Info: [],
      echarts5Info: [],
      echarts6Info: [],
    }
  },
  created () {
    let date1 = new Date();
    let date2 = new Date(date1);
    date2.setDate(date1.getDate() - 30);
    this.dateTime[0] = `${date2.getFullYear()}-${date2.getMonth() + 1 < 10 ? `0${date2.getMonth() + 1}` : date2.getMonth() + 1}-${date2.getDate()}`;
    this.dateTime[1] = `${date1.getFullYear()}-${date1.getMonth() + 1 < 10 ? `0${date1.getMonth() + 1}` : date1.getMonth() + 1}-${date1.getDate()}`;
    this.getInfo()
    this.searchInfo()
  },
  mounted () { },
  methods: {
    async getInfo () {
      await getHomeNumAll()
        .then(res => {
          if (res.code === 200) {
            this.mainCardData[0].leftNum = res.data.all.yearts
            this.mainCardData[0].rightNum = res.data.all.yearjb
            this.mainCardData[1].leftNum = res.data.all.mouthts
            this.mainCardData[1].rightNum = res.data.all.mouthjb
            this.mainCardData[2].leftNum = res.data.all.dayts
            this.mainCardData[2].rightNum = res.data.all.dayjb
            this.mainCardData[3].leftNum = res.data.all.waitts
            this.mainCardData[3].rightNum = res.data.all.waitjb
          } else {
            this.$message({
              message: `${res.msg}`,
              type: 'error'
            });
          }
        }).catch((err) => {
          console.log(err);
        })
      await getHomePieChart({ startTime: '', endTime: '' })
        .then((res) => {
          if (res.code === 200) {
            let data, data2, myChart1, myChart2;
            data = JSON.parse(JSON.stringify(res.data.ts))
            this.getEchartsData(data, this.echarts1Info)
            myChart1 = this.$echarts.init(document.getElementById("echarts1"));
            if (this.$store.getters.roles != 'zwbjgc') {
              data2 = JSON.parse(JSON.stringify(res.data.jb))
              this.getEchartsData(data2, this.echarts2Info)
              myChart2 = this.$echarts.init(document.getElementById("echarts2"));
            }
            this.$nextTick(() => {
              this.bingtu('投诉', false, ['50%', '60%'], '70%', this.echarts1Info, myChart1)
              if (this.$store.getters.roles != 'zwbjgc') {
                this.bingtu('举报', false, ['50%', '60%'], '70%', this.echarts2Info, myChart2)
              }
            })
          } else {
            this.$message({
              message: `${res.msg}`,
              type: 'error'
            });
          }
        })
        .catch((err) => {
          console.log(err);
        })
      await getHomeLineChart()
        .then((res) => {
          if (res.code === 200) {
            this.$nextTick(() => {
              let myChart3 = this.$echarts.init(document.getElementById("echarts3"));
              let data = ['投诉', '举报']
              if (this.$store.getters.roles == 'zwbjgc') {
                data = ['投诉']
              }
              this.zhexian(myChart3, data, res.data.name, res.data.jbcount, res.data.tscount)
            })
          } else {
            this.$message({
              message: `${res.msg}`,
              type: 'error'
            });
          }
        })
        .catch((err) => {
          console.log(err);
        })
      await getHomeBarChart()
        .then((res) => {
          if (res.code === 200) {
            this.$nextTick(() => {
              if (this.$store.getters.roles != 'zwbjgc') {
                let myChart4 = this.$echarts.init(document.getElementById("echarts4"));
                this.zhuzhuang(myChart4, res.data.name, res.data.dsjjNum, res.data.zwbNum)
              }
            })
          } else {
            this.$message({
              message: `${res.msg}`,
              type: 'error'
            });
          }
        })
        .catch((err) => {
          console.log(err);
        })
    },
    // aside信息
    searchInfo () {
      let time = {}
      if (this.dateTime) {
        time = { startTime: this.dateTime[0], endTime: this.dateTime[1] }
      } else {
        time = { startTime: '', endTime: '' }
      }
      getHomePieChartByTime(time)
        .then((res) => {
          if (res.code === 200) {
            if (res.data.num) {
              this.asideCardData[0].num = res.data.num.tscount
              if (this.$store.getters.roles != 'zwbjgc') {
                this.asideCardData[1].num = res.data.num.jbcount
              } else {
                this.asideCardData.length = 1
              }
            }
            let data, data2, myChart5, myChart6;
            data = JSON.parse(JSON.stringify(res.data.ts))
            this.echarts5Info = []
            this.getEchartsData(data, this.echarts5Info)
            myChart5 = this.$echarts.init(document.getElementById("echarts5"));
            this.$nextTick(() => {
              this.bingtu('投诉', false, ['55%', '60%'], '50%', this.echarts5Info, myChart5)
              if (this.$store.getters.roles != 'zwbjgc') {
                data2 = JSON.parse(JSON.stringify(res.data.jb))
                this.echarts6Info = []
                this.getEchartsData(data2, this.echarts6Info)
                myChart6 = this.$echarts.init(document.getElementById("echarts6"));
                this.bingtu('举报', false, ['55%', '60%'], '50%', this.echarts6Info, myChart6)
              }
            })
          } else {
            this.$message({
              message: `${res.msg}`,
              type: 'error'
            });
          }
        })
        .catch((err) => {
          console.log(err);
        })
    },
    // 封装接口获取echarts数据
    getEchartsData (data, customName) {
      // let colorArr = ['#006eff', '#f46363', '#2ed477']
      for (let key in data) {
        // if (data[key] != 0) {
        customName.push({ name: key, value: data[key] })
        // }
      }
      // let len = Object.keys(data)
      // for (let i = 0; i < len; i++) {
      //   customName[i].itemStyle = {
      //     normal: { color: colorArr[i] }
      //   }
      // }
    },
    // 公共饼图
    bingtu (title, showLegend, location, size, data, idName) {
      let option = {
        title: {
          text: title,
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          show: showLegend,
        },
        color: ['#FF8787', '#9775FA', '#4DABF7', '#38D9A9', '#A9E34B', '#FFA94D', '#DA77F2', '#748FFC', '#3BC9DB', '#69DB7C', '#FFD43B'],
        series: [
          {
            name: title,
            center: location,
            type: 'pie',
            radius: size,
            data: data,
            // label: {
            //   formatter: '{b}:{c}: ({d}%)',
            // },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      idName.setOption(option);
      window.addEventListener("resize", function () {
        idName.resize();
      });
    },
    // 折线图
    zhexian (idName, legend, xName, jbData, tsData) {
      let option = {
        title: {
          text: ''
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: legend
        },
        color: ['#006eff', '#f46363'],
        grid: {
          left: '5%',
          right: '7%',
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xName,
          axisTick: {
            show: true
          },
        },
        yAxis: {
          type: 'value',
          name: '数量：个',
        },
        series: [
          {
            name: '投诉',
            type: 'line',
            data: tsData,
          },
          {
            name: '举报',
            type: 'line',
            data: jbData
          },
        ]
      };
      idName.setOption(option);
      window.addEventListener("resize", function () {
        idName.resize();
      });
    },
    // 柱状图
    zhuzhuang (idName, xName, dsjjNum, zwbNum) {
      let option = {
        title: {
          text: '',
          textStyle: {
            fontSize: '12px'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: "shadow"
          }
        },
        legend: {
          show: true
        },
        color: ['#006eff', '#f46363'],
        grid: {
          left: '5%',
          right: '5%',
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xName,
        },
        yAxis: {
          type: 'value',
          name: '数量：个'
        },
        series: [{
          name: '大数据局',
          type: 'bar',
          data: dsjjNum,
        },
        {
          name: '政务办',
          type: 'bar',
          data: zwbNum,
        }
        ]
      }
      idName.setOption(option)
      window.addEventListener("resize", function () {
        idName.resize();
      });
    },
    // 导出按钮
    exportFile () {
      this.download('/system/report/exportStatistics', {
        startTime: this.dateTime[0], endTime: this.dateTime[1]
      }, `码上监督统计报表.xlsx`)
    },
  },
  beforeDestroy () {
    location.reload()
  }
};
</script>

<style scoped lang="scss">
.app_container {
  min-width: 1040px;
  .el-main {
    padding: 16px;
    .top_card {
      display: flex;
      justify-content: space-between;
      margin: 0 -14px 16px;
      .el-col-6 {
        width: 23.5%;
      }
    }
    .main_echarts {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      margin: 0 3px;
      .el-card {
        width: 49.5%;
        height: auto;
        &:nth-child(1),
        &:nth-child(2) {
          margin-bottom: 16px;
        }
        .echarts_box {
          width: 100%;
          height: 296px;
        }
      }
    }
  }
  .box_card {
    background: #f2f2f2;
    text-align: center;
    border: 1px solid #d3d3d3;
    border-radius: 6px;
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
    cursor: default;
    .card_title {
      height: 50px;
      line-height: 50px;
      border-bottom: 1px solid rgb(232, 232, 232);
      background: #fff;
      border-radius: 6px;
    }
    .card_content {
      height: 70px;
      display: flex;
      .left_box,
      .right_box {
        width: 100%;
        height: 100%;
        font-size: 22px;
        color: #f46363;
        font-weight: bold;
        display: flex;
        flex-direction: column;
        justify-content: center;
        p {
          margin: 0;
        }
        .text {
          font-size: 12px;
          font-weight: 400;
          color: #4d4d4d;
          margin-bottom: 6px;
        }
      }
      .left_box {
        color: #006eff;
        border-right: 1px solid rgb(232, 232, 232);
      }
    }
  }
  .el-aside {
    overflow: visible;
    padding: 14px 16px;
    margin-bottom: 0;

    .el-card {
      overflow: visible;
    }
    .yellow_btn {
      width: 100%;
      margin-bottom: 12px;
    }
    .select_date {
      margin-bottom: 16px;
      .el-date-editor {
        width: calc(360px - 32px);
      }
    }
    .aside_card {
      display: flex;
      justify-content: space-between;
      margin-bottom: 16px;
      .box_card {
        width: 100%;
        background: #f2f2f2;
        &:nth-child(2n) {
          margin-left: 16px;
        }
        .card_title,
        .card_content {
          width: 100%;
          height: 46px;
          line-height: 46px;
          .num {
            margin: 0 auto;
            font-size: 22px;
            color: #006eff;
          }
        }
        &:last-child {
          .num {
            color: #f46363;
          }
        }
      }
    }
    .aside_echarts {
      .echarts_box {
        width: 100%;
        height: 265px;
      }
    }
  }
}
</style>

