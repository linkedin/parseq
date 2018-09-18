module.exports = {
  trace: {
    parse: require("./lib/trace/parse"),
    excludeUserTasks: require("./lib/trace/exclude").userTasks,
    excludeSystemTasks: require("./lib/trace/exclude").systemTasks,
    excludeParentTasks: require("./lib/trace/exclude").parentTasks,
    excludeNonCriticalTasks: require("./lib/trace/exclude").nonCriticalTasks,
    criticalPath: require("./lib/trace/criticalPath").computeCriticalPath
  },
  renderGraphviz: require("./lib/render/graphviz"),
  renderTable: require("./lib/render/table"),
  renderWaterfall: require("./lib/render/waterfall")
};
