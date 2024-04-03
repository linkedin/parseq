import process from 'node:process';
import path from 'node:path';
import fs from 'node:fs';
import puppeteer from 'puppeteer';

const json = '{"traces":[{"id":11,"name":"getNetwork","resultType":"SUCCESS","hidden":false,"systemHidden":false,"value":"com.linkedin.parseq.example.composite.classifier.Network@e8234ec","startNanos":1348699279670920000,"endNanos":1348699279796287000},{"id":0,"name":"ClassifierPlan[viewerId=0]","resultType":"SUCCESS","hidden":false,"systemHidden":false,"value":"{0=FULL_VISIBILITY, 1=FULL_VISIBILITY, 2=FULL_VISIBILITY, 3=NO_VISIBILITY, 4=PARTIAL_VISIBILITY, 5=NO_VISIBILITY, 6=NO_VISIBILITY, 7=NO_VISIBILITY, 8=FULL_VISIBILITY, 9=PARTIAL_VISIBILITY, 10=NO_VISIBILITY, 11=FULL_VISIBILITY, 12=FULL_VISIBILITY, 13=NO_VISIBILITY, 14=PARTIAL_VISIBILITY, 15=NO_VISIBILITY, 17=NO_VISIBILITY, 16=NO_VISIBILITY, 19=PARTIAL_VISIBILITY, 18=FULL_VISIBILITY}","startNanos":1348699279669004000,"endNanos":1348699279830978000},{"id":7,"name":"CommonGroupsClassifier","resultType":"SUCCESS","hidden":false,"systemHidden":false,"startNanos":1348699279830399000,"endNanos":1348699279830519000},{"id":5,"name":"seq","resultType":"SUCCESS","hidden":false,"systemHidden":true,"startNanos":1348699279797395000,"endNanos":1348699279830537000},{"id":19,"name":"MessagedClassifier","resultType":"SUCCESS","hidden":false,"systemHidden":false,"startNanos":1348699279682969000,"endNanos":1348699279683090000},{"id":16,"name":"getGroupInvited","resultType":"SUCCESS","hidden":false,"systemHidden":false,"value":"{1=true, 3=false, 4=false, 5=false, 6=false, 7=false, 8=false, 9=false, 10=false, 11=true, 13=false, 14=false, 15=false, 17=false, 16=false, 19=false, 18=false}","startNanos":1348699279671238000,"endNanos":1348699279794374000},{"id":1,"name":"seq","resultType":"EARLY_FINISH","hidden":false,"systemHidden":true,"startNanos":1348699279669575000,"endNanos":1348699279830999000},{"id":6,"name":"getCommonGroups","resultType":"SUCCESS","hidden":false,"systemHidden":false,"value":"{17=false, 16=false, 3=false, 4=true, 5=false, 6=false, 7=false, 10=false, 13=false, 14=true, 15=false}","startNanos":1348699279797600000,"endNanos":1348699279830240000},{"id":18,"name":"getMessaged","resultType":"SUCCESS","hidden":false,"systemHidden":false,"value":"{1=false, 2=true, 3=false, 4=false, 5=false, 6=false, 7=false, 8=false, 9=false, 10=false, 11=false, 12=true, 13=false, 14=false, 15=false, 17=false, 16=false, 19=false, 18=false}","startNanos":1348699279671430000,"endNanos":1348699279682730000},{"id":13,"name":"seq","resultType":"SUCCESS","hidden":false,"systemHidden":true,"startNanos":1348699279670603000,"endNanos":1348699279794866000},{"id":12,"name":"ConnectedClassifier","resultType":"SUCCESS","hidden":false,"systemHidden":false,"startNanos":1348699279796436000,"endNanos":1348699279796570000},{"id":17,"name":"seq","resultType":"SUCCESS","hidden":false,"systemHidden":true,"startNanos":1348699279670759000,"endNanos":1348699279683116000},{"id":2,"name":"SelfClassifier","resultType":"SUCCESS","hidden":false,"systemHidden":false,"startNanos":1348699279669936000,"endNanos":1348699279670042000},{"id":20,"name":"par","resultType":"SUCCESS","hidden":false,"systemHidden":true,"value":"[null, null, null]","startNanos":1348699279670192000,"endNanos":1348699279796671000},{"id":21,"name":"par","resultType":"SUCCESS","hidden":false,"systemHidden":true,"value":"[null, null]","startNanos":1348699279796972000,"endNanos":1348699279830607000},{"id":10,"name":"seq","resultType":"SUCCESS","hidden":false,"systemHidden":true,"startNanos":1348699279670437000,"endNanos":1348699279796588000},{"id":3,"name":"DefaultClassifier","resultType":"SUCCESS","hidden":false,"systemHidden":false,"startNanos":1348699279830880000,"endNanos":1348699279831174000},{"id":8,"name":"NetworkClassifier","resultType":"SUCCESS","hidden":false,"systemHidden":false,"startNanos":1348699279797158000,"endNanos":1348699279797251000},{"id":14,"name":"GroupInvitedClassifier","resultType":"SUCCESS","hidden":false,"systemHidden":false,"startNanos":1348699279794686000,"endNanos":1348699279794824000}],"relationships":[{"relationship":"PARENT_OF","from":0,"to":1},{"relationship":"SUCCESSOR_OF","from":7,"to":6},{"relationship":"PARENT_OF","from":5,"to":7},{"relationship":"PARENT_OF","from":5,"to":6},{"relationship":"SUCCESSOR_OF","from":19,"to":18},{"relationship":"PARENT_OF","from":1,"to":21},{"relationship":"PARENT_OF","from":1,"to":20},{"relationship":"PARENT_OF","from":1,"to":3},{"relationship":"PARENT_OF","from":1,"to":2},{"relationship":"PARENT_OF","from":13,"to":16},{"relationship":"PARENT_OF","from":13,"to":14},{"relationship":"SUCCESSOR_OF","from":12,"to":11},{"relationship":"PARENT_OF","from":17,"to":19},{"relationship":"PARENT_OF","from":17,"to":18},{"relationship":"PARENT_OF","from":20,"to":17},{"relationship":"SUCCESSOR_OF","from":20,"to":2},{"relationship":"PARENT_OF","from":20,"to":13},{"relationship":"PARENT_OF","from":20,"to":10},{"relationship":"SUCCESSOR_OF","from":21,"to":20},{"relationship":"PARENT_OF","from":21,"to":8},{"relationship":"PARENT_OF","from":21,"to":5},{"relationship":"PARENT_OF","from":10,"to":12},{"relationship":"PARENT_OF","from":10,"to":11},{"relationship":"SUCCESSOR_OF","from":3,"to":21},{"relationship":"SUCCESSOR_OF","from":14,"to":16}]}';

(async () => {
  const browser = await puppeteer.launch({ headless: 'new' });
  const page = await browser.newPage();

  await page.goto('file://' + path.join(
    process.cwd(),
    'build/instrumentation/parseq-tracevis/trace.html'
  ));

  for (const view of ['waterfall', 'graphviz', 'table']) {
    console.log('Testing view: ' + view);

    await page.evaluate(function(json, view) {
      renderTrace(json, view);
    }, json, view);
  }

  const coverageDir = path.join(process.cwd(), 'build/coverage/int-test/');

  // Ensure write directory exists.
  fs.mkdirSync(
    coverageDir,
    { recursive: true }
  );

  fs.writeFileSync(
    path.join(coverageDir, 'coverage.json'),
    JSON.stringify(await page.evaluate(function() {
      return __coverage__;
    }))
  );

  await browser.close();
})();