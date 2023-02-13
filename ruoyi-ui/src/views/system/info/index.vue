<template>
  <div class="info_container">
    <div class="first_line" v-show="showSearch">
      <el-form :model="searchOptions" label-width="70px" size="small">
        <el-form-item label="办结情况">
          <el-select v-model="searchOptions.isSuccess" placeholder="请选择办结情况" @change="searchTableData">
            <el-option v-for="item in isSuccessOptions" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="服务类别">
          <el-select v-model="searchOptions.serviceType" placeholder="请选择服务类别" @change="searchTableData">
            <el-option v-for="item in serviceTypeOptions" :key="item.value" :label="item.dictLabel" :value="item.dictValue">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投诉问题">
          <el-select v-model="searchOptions.detailInfo" placeholder="请选择投诉问题" @change="searchTableData">
            <el-option v-for="item in detailOptions" :key="item.value" :label="item.dictLabel" :value="item.dictValue">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="事件时间">
          <el-date-picker v-model="searchOptions.dateTime" type="daterange" size="small" unlink-panels range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" :picker-options="pickerOptions" @change="searchTableData" />
        </el-form-item>
        <el-form-item label="内容搜索">
          <el-input v-model="searchOptions.content" placeholder="搜索投诉内容" prefix-icon="el-icon-search" clearable @input="searchTableData"></el-input>
        </el-form-item>
        <div style="margin-bottom:16px">
          <!-- <el-button @click="searchTableData" type="primary" icon="el-icon-search" size="small">搜索</el-button> -->
          <el-button @click="resetList" icon="el-icon-refresh-right" size="small">重置</el-button>
        </div>
      </el-form>
    </div>
    <div class="second_line">
      <el-button @click="exportFile" type="warning" icon="el-icon-download" size="small" class="yellow_btn">导出</el-button>
      <div class="right_btn">
        <el-tooltip class="item" effect="dark" :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top">
          <el-button size="mini" circle icon="el-icon-search" @click="showSearch = !showSearch" />
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="刷新" placement="top">
          <el-button size="mini" circle icon="el-icon-refresh" @click="resetList()" />
        </el-tooltip>
      </div>
    </div>
    <el-table ref="multipleTable" :data="tableData" tooltip-effect="light" style="width: 100%" v-loading="tableLoading" element-loading-spinner="el-icon-loading">
      <el-table-column type="index" width="60">
      </el-table-column>
      <el-table-column prop="serviceType" label="服务类别">
      </el-table-column>
      <el-table-column prop="windowNum" label="窗口号码" width="100" align="center" />
      <el-table-column prop="detailInfo" label="投诉问题" align="center" />
      <el-table-column prop="content" label="投诉内容" show-overflow-tooltip align="center" />
      <el-table-column label="是否实名" align="center" width="100">
        <template slot-scope="scope">{{ scope.row.isReal == 'N'?'否':'是' }}</template>
      </el-table-column>
      <el-table-column prop="evaluate" label="用户反馈">
        <template slot-scope="scope">{{scope.row.reportResultList[0].evaluate?scope.row.reportResultList[0].evaluate:'- -' }}</template>
      </el-table-column>
      <el-table-column label="办结情况" width="100" align="center">
        <template slot-scope="scope" v-if="scope.row.reportResultList">
          <span :style="{color:scope.row.reportResultList[0].isSuccess == 'N'?'red':'#606266'}">{{scope.row.reportResultList[0].isSuccess == 'N'?'待处理':'已办结' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="办结时长" width="100" align="center">
        <template slot-scope="scope">
          <span>{{scope.row.completionTime==null?'- -':(scope.row.completionTime==0?'当天':scope.row.completionTime+'天')}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="投诉时间" />
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-link v-if="($store.getters.roles == 'common' || $store.getters.roles == 'jjzAdmin') && scope.row.reportResultList[0].process == '1' && scope.row.reportResultList[0].isSuccess == 'N'" @click="openDialog(scope.row,'dispose')" type="primary" :underline="false" icon="el-icon-edit">处理</el-link>
          <el-link v-if="$store.getters.roles == 'zwbjgc' && scope.row.reportResultList[0].process == '2' && scope.row.reportResultList[0].isSuccess == 'N'" @click="openDialog(scope.row,'dispose')" type="primary" :underline="false" icon="el-icon-edit">处理</el-link>
          <el-link v-if="$store.getters.roles == 'swzx' && scope.row.reportResultList[0].process == '3' && scope.row.reportResultList[0].handResults==null" @click="openDialog(scope.row,'handle')" type="primary" :underline="false" icon="el-icon-edit">办理</el-link>
          <el-link v-if="$store.getters.roles != 'admin' && scope.row.reportResultList[0].process == '1' && scope.row.reportResultList[0].isSuccess == 'N'" @click="issueList(scope.row)" type="primary" :underline="false" icon="el-icon-sort-down">下派</el-link>
          <el-link v-if="$store.getters.roles == 'zwbjgc' && scope.row.reportResultList[0].process == '2' && scope.row.reportResultList[0].isSuccess == 'N'" @click="toHandOver(scope.row)" type="primary" :underline="false" icon="el-icon-sort-down">交办</el-link>
          <el-link @click="openDialog(scope.row,'toView')" type="primary" :underline="false" icon="el-icon-view">查看</el-link>
          <el-link v-if="$store.getters.roles != 'zwbjgc' && $store.getters.roles != 'swzx'" type="primary" @click="exportAsingleFile(scope.row)" :underline="false" icon="el-icon-download">导出</el-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="searchOptions.pageNum" :limit.sync="searchOptions.pageSize" @pagination="getTableData" />
    <el-dialog :visible.sync="showDialog" :title="dialogTitle" width="560px" @close="closeDialog">
      <el-form :model="currentData" :rules="rules" ref="ruleForm" label-width="110px" label-position="right">
        <el-form-item label="服务类别:">
          {{currentData.serviceType}}
        </el-form-item>
        <el-form-item label="投诉内容:">
          {{currentData.detailInfo}}
        </el-form-item>
        <el-form-item label="窗口号码:">
          {{currentData.windowNum}}
        </el-form-item>
        <el-form-item label="详情信息:">
          {{currentData.content}}
        </el-form-item>
        <el-form-item label="文件信息:" class="img_list">
          <div class="dialog_img">
            <el-image :src="item" :preview-src-list="imgList" v-for="(item,index) in imgList" :key="item+index+'imgList'" class="img_list">
            </el-image>
          </div>
          <div class="dialog_img">
            <video :src="item" @click="openVideo(item)" v-for="(item,index) in videoList" :key="item.url+index+'videoList'" class="img_list"></video>
          </div>
          <div v-for="(item,index) in fileList" :key="item.fileName+index">
            <a style="color:#3093d9" :href="item.fileUrl">{{item.fileName}} </a>
          </div>
        </el-form-item>
        <el-form-item label="姓名/电话:">
          {{currentData.isReal=='Y'?`${currentData.realName}/${currentData.phone}`:'匿名'}}
        </el-form-item>
        <el-form-item v-if="dialogTitle!='查看'" :label="dialogTitle+'意见:'" prop="opinion" key="opinion">
          <el-input v-model="currentData.opinion" type="textarea" :autosize="{minRows: 3, maxRows: 6}" maxlength="500" :placeholder="'请输入'+dialogTitle+'意见'" />
        </el-form-item>
        <el-form-item v-else label="处理结果:">
          {{currentData.opinion}}
        </el-form-item>
        <el-form-item label="事件处理时长:">
          {{currentData.completionTime==null?'- -':(currentData.completionTime==0?'当天':currentData.completionTime+'天')}}
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showDialog = false;currentData.opinion == ''" size="small">{{dialogTitle!='查看'?'取 消':'关 闭'}}</el-button>
        <el-button v-if="dialogTitle!='查看'" type="primary" @click="sureSubmit" size="small">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInfo, downInfo, submitReport, getDicAll, fileListById, handOver, reportUserHand } from "@/api/system/info";
export default {
  name: 'info',
  data () {
    return {
      searchOptions: {
        pageNum: 1,
        pageSize: 10,
        isSuccess: '',
        content: '',
        serviceType: '',
        detailInfo: '',
        dateTime: ['', '']
      },
      isSuccessOptions: [
        {
          "label": "办理中",
          "value": 'N'
        }, {
          "label": "已办结",
          "value": 'Y'
        }],


      serviceTypeOptions: [],
      detailOptions: [],
      showSearch: true,
      tableData: [],
      tableLoading: true,
      total: 0,
      showDialog: false,
      dialogTitle: '',
      currentData: {
        id: '',
        opinion: '',
      },
      timer: null,
      imgList: [],
      videoList: [],
      fileList: [],
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
      rules: {
        opinion: [{ type: 'string', required: true, message: '请输入处理意见', trigger: 'blur', }],
      },
    }
  },
  created () {
    this.getSelectList();
    this.getTableData()
  },
  methods: {
    // 下拉框数据
    getSelectList () {
      getDicAll().then(res => {
        this.serviceTypeOptions = res.data.service_type
        this.detailOptions = res.data.detail_info
      });
    },
    // 表格数据
    getTableData () {
      this.tableLoading = true
      let data = {
        pageNum: this.searchOptions.pageNum,
        pageSize: this.searchOptions.pageSize,
        isSuccess: this.searchOptions.isSuccess,
        content: this.searchOptions.content,
        serviceType: this.searchOptions.serviceType,
        detailInfo: this.searchOptions.detailInfo,
        startTime: this.searchOptions.dateTime[0],
        endTime: this.searchOptions.dateTime[1],
      }
      listInfo(data).then(res => {
        this.tableData = res.rows;
        this.tableLoading = false
        this.total = res.total;
      });
    },
    // 搜索按钮
    searchTableData () {
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.searchOptions.pageNum = 1
        this.getTableData()
      }, 500);
    },
    // 重置按钮
    resetList () {
      this.searchOptions = {
        pageNum: 1,
        pageSize: 10,
        isSuccess: '',
        serviceType: '',
        detailInfo: '',
        dateTime: ['', '']
      }
      this.getTableData()
    },
    // 导出所有
    exportFile () {
      this.download('system/report/export', {}, `码上监督投诉报表.xlsx`)
    },
    // 导出单个
    exportAsingleFile (row) {
      this.download('system/report/exportDocx', {
        id: row.id
      }, `投诉信息详情.docx`)
    },
    // 打开弹窗
    openDialog (row, title) {
      title == 'dispose' ? this.dialogTitle = '处理' : (title == 'toView' ? this.dialogTitle = '查看' : this.dialogTitle = '办理')
      this.currentData = row;
      this.currentData.id = row.id;
      if (title == 'toView') this.currentData.opinion = row.reportResultList[0].opinion
      fileListById({ id: row.id }).then(res => {
        this.imgList = []
        this.videoList = []
        this.fileList = []
        let imgFormat = ['.jpg', '.png', '.gif', '.bmp', '.jpeg', '.tiff', '.svg']
        if (res.data) {
          res.data.map(item => {
            if (item.extension == '.mp4') {
              this.videoList.push(item.url)
            } else if (imgFormat.indexOf(item.extension) != -1) {
              this.imgList.push(item.url)
            } else {
              this.fileList.push({
                fileName: item.fileName,
                fileUrl: item.url
              })
            }
          })
        }
        this.showDialog = true
      })
    },
    // 公共方法
    publicFun (res) {
      if (res.code == 200) {
        this.getTableData()
        this.$message({
          message: `${res.msg}`,
          type: 'success'
        });
        this.showDialog = false
      } else {
        this.$message({
          message: `${res.msg}`,
          type: 'error'
        });
      }
    },
    // 确定处理
    sureSubmit () {
      if (this.dialogTitle != '查看') {
        let data = { id: this.currentData.id, opinion: this.currentData.opinion }
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            if (this.dialogTitle == '处理') {
              submitReport(data).then((res) => {
                this.publicFun(res)
              })
            } else {
              reportUserHand(data).then((res) => {
                this.publicFun(res)
              })
            }
          } else {
            return false;
          }
        });
      } else {
        this.showDialog = false
      }

    },
    // 新窗口打开视频
    openVideo (url) {
      let a = document.createElement("a");
      document.body.appendChild(a);
      a.style = "display: none";
      a.target = "_blank";
      a.href = url;
      a.click();
      document.body.removeChild(a);
    },
    // 关闭弹窗
    closeDialog () {
      this.$nextTick(() => {
        this.currentData.opinion = ''
        if (this.dialogTitle != '查看') {
          this.$refs.ruleForm.resetFields();
        }
      })
    },
    // 下派
    issueList (row) {
      this.$modal.confirm(`是否确认下派投诉详情编号为${row.id ? row.id : '   '}的数据项？`).then(() => {
        downInfo(row.id).then((res) => {
          this.publicFun(res)
        })
      }).catch(() => { });
    },
    //交办
    toHandOver (row) {
      this.$modal.confirm(`是否确认交办投诉详情编号为${row.id ? row.id : '   '}的数据项？`).then(() => {
        handOver(row.id).then((res) => {
          this.publicFun(res)
        })
      }).catch(() => { });
    }
  }
}
</script>

<style lang="scss" scoped>
.info_container {
  padding: 16px;
  .el-date-editor {
    width: 215px;
  }
  .first_line {
    .el-form {
      display: flex;
      flex-wrap: wrap;
      display: flex;
      .el-form-item {
        margin-right: 12px;
      }
      .el-form-item__label {
        padding: 0;
      }
      .el-input .el-input--small {
        width: 180px;
      }
      .el-button {
        height: 32px;
      }
    }
  }
  .second_line {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    .yellow_btn {
      color: #ffc077;
      background: #fff8e6;
      border: 1px solid #f9dc8f;
    }
  }
  .el-link {
    margin-left: 12px;
  }
  .dialog_img {
    display: flex;
    justify-content: flex-start;
    flex-wrap: wrap;
    .img_list {
      width: 100%;
      height: 100%;
      max-width: 120px;
      margin: 0 12px 12px 0;
    }
  }
}
</style>