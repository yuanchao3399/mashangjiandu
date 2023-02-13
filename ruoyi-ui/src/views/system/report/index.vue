<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="${comment}" prop="realName">
        <el-input v-model="queryParams.realName" placeholder="请输入${comment}" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="${comment}" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入${comment}" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="${comment}" prop="spare1">
        <el-input v-model="queryParams.spare1" placeholder="请输入${comment}" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="${comment}" prop="spare2">
        <el-input v-model="queryParams.spare2" placeholder="请输入${comment}" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="${comment}" prop="spare3">
        <el-input v-model="queryParams.spare3" placeholder="请输入${comment}" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:report:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:report:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:report:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['system:report:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reportList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="${comment}" align="center" prop="serviceType" />
      <el-table-column label="${comment}" align="center" prop="windowNum" />
      <el-table-column label="${comment}" align="center" prop="content" />
      <el-table-column label="${comment}" align="center" prop="type" />
      <el-table-column label="${comment}" align="center" prop="fileId" />
      <el-table-column label="${comment}" align="center" prop="isReal" />
      <el-table-column label="${comment}" align="center" prop="realName" />
      <el-table-column label="${comment}" align="center" prop="phone" />
      <el-table-column label="${comment}" align="center" prop="spare1" />
      <el-table-column label="${comment}" align="center" prop="spare2" />
      <el-table-column label="${comment}" align="center" prop="spare3" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:report:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['system:report:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改投诉/举报信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="${comment}" prop="windowNum">
          <el-input v-model="form.windowNum" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}">
          <editor v-model="form.content" :min-height="192" />
        </el-form-item>
        <el-form-item label="${comment}" prop="fileId">
          <el-input v-model="form.fileId" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="isReal">
          <el-input v-model="form.isReal" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="spare1">
          <el-input v-model="form.spare1" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="spare2">
          <el-input v-model="form.spare2" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="spare3">
          <el-input v-model="form.spare3" placeholder="请输入${comment}" />
        </el-form-item>
        <el-divider content-position="center">投诉/举报结果信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddReportResult">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteReportResult">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="reportResultList" :row-class-name="rowReportResultIndex" @selection-change="handleReportResultSelectionChange" ref="reportResult">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50" />
          <el-table-column label="$comment" prop="reportId" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.reportId" placeholder="请输入$comment" />
            </template>
          </el-table-column>
          <el-table-column label="$comment" prop="process" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.process" placeholder="请输入$comment" />
            </template>
          </el-table-column>
          <el-table-column label="$comment" prop="isSuccess" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.isSuccess" placeholder="请输入$comment" />
            </template>
          </el-table-column>
          <el-table-column label="$comment" prop="evaluate" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.evaluate" placeholder="请输入$comment" />
            </template>
          </el-table-column>
          <el-table-column label="$comment" prop="updateUser" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.updateUser" placeholder="请输入$comment" />
            </template>
          </el-table-column>
          <el-table-column label="$comment" prop="spare1" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.spare1" placeholder="请输入$comment" />
            </template>
          </el-table-column>
          <el-table-column label="$comment" prop="spare2" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.spare2" placeholder="请输入$comment" />
            </template>
          </el-table-column>
          <el-table-column label="$comment" prop="spare3" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.spare3" placeholder="请输入$comment" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReport, getReport, delReport, addReport, updateReport } from "@/api/system/report";

export default {
  name: "Report",
  data () {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedReportResult: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 投诉/举报信息表格数据
      reportList: [],
      // 投诉/举报结果表格数据
      reportResultList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serviceType: null,
        windowNum: null,
        content: null,
        type: null,
        fileId: null,
        isReal: null,
        realName: null,
        phone: null,
        spare1: null,
        spare2: null,
        spare3: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created () {
    this.getList();
  },
  methods: {
    /** 查询投诉/举报信息列表 */
    getList () {
      this.loading = true;
      listReport(this.queryParams).then(response => {
        this.reportList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel () {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset () {
      this.form = {
        id: null,
        serviceType: null,
        windowNum: null,
        content: null,
        type: null,
        fileId: null,
        isReal: null,
        realName: null,
        phone: null,
        createTime: null,
        updateTime: null,
        spare1: null,
        spare2: null,
        spare3: null
      };
      this.reportResultList = [];
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery () {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery () {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange (selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd () {
      this.reset();
      this.open = true;
      this.title = "添加投诉/举报信息";
    },
    /** 修改按钮操作 */
    handleUpdate (row) {
      this.reset();
      const id = row.id || this.ids
      getReport(id).then(response => {
        this.form = response.data;
        this.reportResultList = response.data.reportResultList;
        this.open = true;
        this.title = "修改投诉/举报信息";
      });
    },
    /** 提交按钮 */
    submitForm () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.reportResultList = this.reportResultList;
          if (this.form.id != null) {
            updateReport(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addReport(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete (row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除投诉/举报信息编号为"' + ids + '"的数据项？').then(function () {
        return delReport(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 投诉/举报结果序号 */
    rowReportResultIndex ({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 投诉/举报结果添加按钮操作 */
    handleAddReportResult () {
      let obj = {};
      obj.reportId = "";
      obj.process = "";
      obj.isSuccess = "";
      obj.opinion = "";
      obj.evaluate = "";
      obj.updateUser = "";
      obj.spare1 = "";
      obj.spare2 = "";
      obj.spare3 = "";
      this.reportResultList.push(obj);
    },
    /** 投诉/举报结果删除按钮操作 */
    handleDeleteReportResult () {
      if (this.checkedReportResult.length == 0) {
        this.$modal.msgError("请先选择要删除的投诉/举报结果数据");
      } else {
        const reportResultList = this.reportResultList;
        const checkedReportResult = this.checkedReportResult;
        this.reportResultList = reportResultList.filter(function (item) {
          return checkedReportResult.indexOf(item.index) == -1
        });
      }
    },
    /** 复选框选中数据 */
    handleReportResultSelectionChange (selection) {
      this.checkedReportResult = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport () {
      this.download('system/report/export', {
        ...this.queryParams
      }, `report_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
