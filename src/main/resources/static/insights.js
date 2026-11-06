/* SkillBridge AI: student progress analytics, personalized roadmap and admin summary. */
(() => {
  const section = document.getElementById('insightsSection');
  const adminSection = document.getElementById('adminInsightsSection');
  if (!section || typeof authFetch !== 'function') return;
  let trendChart;
  const escape = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  const metric = (label, value) => `<div class="col-6 col-md-3"><div class="border rounded-3 p-3 bg-light"><small class="text-muted d-block">${escape(label)}</small><strong class="fs-4">${escape(value)}</strong></div></div>`;

  async function loadInsights() {
    if (!isLoggedIn()) { section.classList.add('d-none'); adminSection?.classList.add('d-none'); return; }
    section.classList.remove('d-none');
    try {
      const [analyticsRes, roadmapRes] = await Promise.all([authFetch('/api/student/insights'), authFetch('/api/student/roadmap')]);
      if (!analyticsRes.ok || !roadmapRes.ok) throw new Error('Unable to load learning insights. Please sign in again.');
      const analytics = await analyticsRes.json();
      const roadmap = await roadmapRes.json();
      document.getElementById('analyticsMessage').textContent = analytics.message || '';
      const trend = analytics.scoreTrend || [];
      const canvas = document.getElementById('readinessTrendChart');
      if (window.Chart && canvas) {
        if (trendChart) trendChart.destroy();
        trendChart = new Chart(canvas, {type:'line', data:{labels:trend.map(x=>x.date), datasets:[{label:'Readiness %', data:trend.map(x=>x.score), tension:.3, fill:true}]}, options:{responsive:true, scales:{y:{min:0,max:100,title:{display:true,text:'Score (%)'}}}, plugins:{legend:{display:false}}}});
      }
      const skills = analytics.skillSummary || [];
      document.getElementById('skillAnalyticsList').innerHTML = skills.length ? `<h4 class="h6 fw-bold mt-3">Skill averages</h4>${skills.map(s=>`<div class="d-flex justify-content-between align-items-center small mb-2"><span>${escape(s.skill)}</span><span class="fw-semibold">${escape(s.averageScore)}% <span class="text-muted">(${escape(s.attempts)} attempt${s.attempts===1?'':'s'})</span></span></div><div class="progress mb-3" style="height:7px"><div class="progress-bar" style="width:${Math.max(0,Math.min(100,Number(s.averageScore)||0))}%"></div></div>`).join('')}` : '<p class="small text-muted">No skill history yet.</p>';
      document.getElementById('roadmapSummary').textContent = roadmap.summary || 'Your roadmap is ready.';
      document.getElementById('roadmapSource').textContent = roadmap.generatedBy || '';
      if (roadmap.aiAdvice) { document.getElementById('roadmapSummary').textContent += ' AI Coach: ' + roadmap.aiAdvice; }
      const tasks = roadmap.tasks || [];
      document.getElementById('roadmapTasks').innerHTML = tasks.length ? tasks.map((t,i)=>`<article class="border rounded-3 p-3 mb-2"><div class="d-flex justify-content-between gap-2"><div><span class="badge bg-warning-subtle text-dark">Priority ${i+1}</span><h4 class="h6 fw-bold mt-2 mb-1">${escape(t.skill)} <span class="text-muted fw-normal">· ${escape(t.currentScore)}% → ${escape(t.target)}%</span></h4><p class="small mb-2">${escape(t.action)}</p><small class="text-muted">${escape(t.estimatedHours)} estimated hours · ${escape(t.level)}</small></div><span class="badge bg-primary-subtle text-primary align-self-start">Gap ${escape(t.gap)}</span></div><a class="btn btn-sm btn-outline-primary mt-2" href="${escape(t.resourceUrl)}" target="_blank" rel="noopener noreferrer">Open learning resource <i class="bi bi-box-arrow-up-right ms-1"></i></a></article>`).join('') : '<div class="alert alert-success small mb-0">No skill gaps found in your latest assessment. Try an advanced assessment or build a project.</div>';
    } catch (e) {
      document.getElementById('analyticsMessage').textContent = e.message;
      document.getElementById('roadmapTasks').innerHTML = `<div class="alert alert-warning small">${escape(e.message)}</div>`;
    }
  }

  async function loadAdmin() {
    if (!isLoggedIn() || getUser()?.role !== 'ADMIN') { adminSection?.classList.add('d-none'); return; }
    adminSection.classList.remove('d-none');
    try {
      const [overviewRes, rowsRes] = await Promise.all([authFetch('/api/admin/career-overview'), authFetch('/api/admin/career-assessments')]);
      if (!overviewRes.ok || !rowsRes.ok) throw new Error('Admin permission or API error.');
      const data = await overviewRes.json(), rows = await rowsRes.json();
      document.getElementById('adminMetrics').innerHTML = metric('Students',data.studentCount)+metric('Career paths',data.careerCount)+metric('Career assessments',data.careerAssessmentCount)+metric('Avg readiness',`${data.averageReadiness}%`);
      document.getElementById('adminAssessmentRows').innerHTML = rows.length ? rows.map(r=>`<tr><td>${escape(r.studentName)}<br><small class="text-muted">${escape(r.studentEmail)}</small></td><td>${escape(r.career)}</td><td>${escape(r.score)}%</td><td>${escape(r.readiness)}%</td><td>${escape(String(r.createdAt).replace('T',' ').slice(0,16))}</td></tr>`).join('') : '<tr><td colspan="5" class="text-muted">No career assessments yet.</td></tr>';
    } catch (e) { document.getElementById('adminAssessmentRows').innerHTML = `<tr><td colspan="5">${escape(e.message)}</td></tr>`; }
  }
  document.getElementById('refreshInsightsBtn')?.addEventListener('click', loadInsights);
  document.getElementById('refreshAdminBtn')?.addEventListener('click', loadAdmin);
  document.addEventListener('DOMContentLoaded', () => { loadInsights(); loadAdmin(); });
  // Auth scripts initialize before/around this script; refresh after login changes.
  window.addEventListener('focus', () => { if (isLoggedIn()) { loadInsights(); loadAdmin(); } });
})();
