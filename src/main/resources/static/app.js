// SkillBridge AI - Adaptive Learning & Skill Gap Analysis
// Client-side Controller, Auth Integration & Dashboard Visualizer

const topicSelect = document.getElementById('topic');
const learnerInput = document.getElementById('learner');
const scoreRange = document.getElementById('score');
const scoreNumber = document.getElementById('scoreNumber');
const scoreBadge = document.getElementById('scoreBadge');
const scoreLevelChip = document.getElementById('scoreLevelChip');
const submitBtn = document.getElementById('submitBtn');
const messageAlert = document.getElementById('messageAlert');
const messageText = document.getElementById('messageText');
const messageIcon = document.getElementById('messageIcon');
const resultContainer = document.getElementById('result');
const historyContainer = document.getElementById('history');
const refreshBtn = document.getElementById('refreshHistory');
const refreshIcon = document.getElementById('refreshIcon');
const topicDetails = document.getElementById('topicDetails');
const topicRoleBadge = document.getElementById('topicRoleBadge');
const topicTargetBadge = document.getElementById('topicTargetBadge');
const historySubtitle = document.getElementById('historySubtitle');
const presetButtons = document.querySelectorAll('.preset-btn');
const btnScoreDec = document.getElementById('btnScoreDec');
const btnScoreInc = document.getElementById('btnScoreInc');
const navAuthContainer = document.getElementById('navAuthContainer');
const studentWelcomeBanner = document.getElementById('studentWelcomeBanner');

let loadedTopics = [];
let debounceTimer = null;

// ==========================================
// 1. Skill Level Classifications & Utilities
// ==========================================
function getLevelInfo(score) {
  if (score >= 80) {
    return { name: 'Strong', badgeClass: 'level-strong', barClass: 'bar-strong' };
  } else if (score >= 60) {
    return { name: 'Intermediate', badgeClass: 'level-intermediate', barClass: 'bar-intermediate' };
  } else if (score >= 40) {
    return { name: 'Needs improvement', badgeClass: 'level-improvement', barClass: 'bar-improvement' };
  } else {
    return { name: 'Beginner', badgeClass: 'level-beginner', barClass: 'bar-beginner' };
  }
}

// ==========================================
// 2. Authentication State & Navbar Rendering
// ==========================================
function initAuthState() {
  if (isLoggedIn()) {
    const user = getUser();
    
    // Update Navbar with student profile & logout button
    navAuthContainer.innerHTML = `
      <div class="d-flex align-items-center gap-2">
        <div class="d-flex align-items-center bg-white bg-opacity-10 px-3 py-1 rounded-pill text-white border border-white-50">
          <i class="bi bi-person-circle text-primary-subtle me-2 fs-5"></i>
          <span class="small fw-semibold me-2">${escapeHtml(user.fullName)}</span>
          <span class="badge ${user.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'} small">${escapeHtml(user.role)}</span>
        </div>
        <button class="btn btn-outline-light btn-sm px-3" id="navLogoutBtn" title="Sign Out">
          <i class="bi bi-box-arrow-right me-1"></i> Sign Out
        </button>
      </div>
    `;

    document.getElementById('navLogoutBtn').addEventListener('click', logout);

    // Auto-fill learner name with logged in user's full name
    learnerInput.value = user.fullName;
    learnerInput.readOnly = true;
    learnerInput.classList.add('bg-light');

    // Show student personalized welcome banner
    studentWelcomeBanner.classList.remove('d-none');
    document.getElementById('dashStudentName').textContent = user.fullName;
    document.getElementById('dashStudentRole').textContent = user.role;
    document.getElementById('dashStudentEmail').textContent = user.email;

    const bannerLogoutBtn = document.getElementById('logoutBtn');
    if (bannerLogoutBtn) {
      bannerLogoutBtn.addEventListener('click', logout);
    }

    // Load full student dashboard metrics
    loadStudentDashboard();
    if (typeof loadCareerHistory === 'function') {
      loadCareerHistory();
    }
  } else {
    // Unauthenticated state
    navAuthContainer.innerHTML = `
      <a href="/login.html" class="btn btn-outline-light btn-sm px-3" id="navLoginBtn">
        <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
      </a>
      <a href="/register.html" class="btn btn-primary btn-sm px-3 ms-1" id="navRegisterBtn">
        <i class="bi bi-person-plus me-1"></i> Register
      </a>
    `;
    studentWelcomeBanner.classList.add('d-none');
    learnerInput.readOnly = false;
    learnerInput.classList.remove('bg-light');
  }
}

async function loadStudentDashboard() {
  try {
    const response = await authFetch('/api/student/dashboard');
    if (!response.ok) return;
    const data = await response.json();

    document.getElementById('dashTotalAssessments').textContent = data.totalAssessments;
    document.getElementById('dashAvgScore').textContent = `${data.averageScore}%`;
    document.getElementById('dashAvgGap').textContent = `${data.averageGap} pts`;

    const levelBadge = document.getElementById('dashOverallLevel');
    const levelInfo = getLevelInfo(Math.round(data.averageScore));
    levelBadge.textContent = `Overall: ${data.currentSkillLevel}`;
    levelBadge.className = `badge level-badge ${levelInfo.badgeClass} px-3 py-2 fs-6`;

    document.getElementById('dashOverallRecommendation').innerHTML = `
      <i class="bi bi-lightbulb-fill text-warning me-1"></i>
      <span>${escapeHtml(data.overallRecommendation)}</span>
    `;
  } catch (err) {
    console.error('Could not load student dashboard stats', err);
  }
}

// ==========================================
// 3. Score Synchronization & Controls
// ==========================================
function setScore(rawValue) {
  let val = parseInt(rawValue, 10);
  if (isNaN(val)) val = 0;
  val = Math.max(0, Math.min(100, val));

  scoreRange.value = val;
  scoreNumber.value = val;
  scoreBadge.textContent = `${val}%`;

  const info = getLevelInfo(val);
  scoreLevelChip.textContent = info.name;
  scoreLevelChip.className = `badge level-badge ${info.badgeClass}`;

  // Update preset button active state
  presetButtons.forEach(btn => {
    if (parseInt(btn.getAttribute('data-score'), 10) === val) {
      btn.classList.add('active', 'btn-outline-primary');
      btn.classList.remove('btn-outline-secondary');
    } else {
      btn.classList.remove('active', 'btn-outline-primary');
      btn.classList.add('btn-outline-secondary');
    }
  });
}

// Sync events across range, number input, and stepper buttons
scoreRange.addEventListener('input', () => setScore(scoreRange.value));
scoreRange.addEventListener('change', () => setScore(scoreRange.value));
scoreNumber.addEventListener('input', () => setScore(scoreNumber.value));
scoreNumber.addEventListener('change', () => setScore(scoreNumber.value));

btnScoreDec.addEventListener('click', () => {
  setScore(parseInt(scoreRange.value, 10) - 5);
});

btnScoreInc.addEventListener('click', () => {
  setScore(parseInt(scoreRange.value, 10) + 5);
});

presetButtons.forEach(btn => {
  btn.addEventListener('click', () => {
    setScore(btn.getAttribute('data-score'));
  });
});

// ==========================================
// 4. User Feedback & Alert System
// ==========================================
function showAlert(type, text) {
  messageAlert.className = `alert alert-banner alert-${type} mt-3 mb-0`;
  messageText.textContent = text;

  if (type === 'success') {
    messageIcon.className = 'bi bi-check-circle-fill text-success me-2 fs-5';
  } else if (type === 'danger') {
    messageIcon.className = 'bi bi-exclamation-triangle-fill text-danger me-2 fs-5';
  } else if (type === 'info') {
    messageIcon.className = 'spinner-border spinner-border-sm text-primary me-2';
  }
  messageAlert.classList.remove('d-none');
}

function hideAlert() {
  messageAlert.classList.add('d-none');
}

// ==========================================
// 5. Topic Loading & Selection
// ==========================================
async function loadTopics() {
  try {
    const response = await fetch('/api/topics');
    if (!response.ok) throw new Error('Unable to connect to topics API');
    loadedTopics = await response.json();

    if (!loadedTopics.length) {
      topicSelect.innerHTML = '<option value="">No topics available</option>';
      return;
    }

    topicSelect.innerHTML = loadedTopics.map(t =>
      `<option value="${t.id}">${escapeHtml(t.name)} (${escapeHtml(t.careerRole)})</option>`
    ).join('');

    updateSelectedTopicDetails();
  } catch (err) {
    topicSelect.innerHTML = '<option value="">Backend unavailable</option>';
    showAlert('danger', 'Could not load topics. Verify that the Spring Boot server is running.');
  }
}

function updateSelectedTopicDetails() {
  const selectedId = Number(topicSelect.value);
  const topic = loadedTopics.find(t => t.id === selectedId);
  if (topic) {
    topicRoleBadge.textContent = topic.careerRole;
    topicTargetBadge.textContent = `Target: ${topic.targetScore}%`;
    topicDetails.classList.remove('d-none');
  } else {
    topicDetails.classList.add('d-none');
  }
}

topicSelect.addEventListener('change', updateSelectedTopicDetails);

// ==========================================
// 6. Assessment Form Submission
// ==========================================
document.getElementById('assessmentForm').addEventListener('submit', async (event) => {
  event.preventDefault();

  const learnerName = learnerInput.value.trim();
  if (!learnerName) {
    showAlert('danger', 'Please enter a valid learner name.');
    learnerInput.focus();
    return;
  }

  const topicId = Number(topicSelect.value);
  if (!topicId) {
    showAlert('danger', 'Please select a skill topic.');
    topicSelect.focus();
    return;
  }

  // Exact submitted score - guaranteed to match the range & input
  const score = Math.max(0, Math.min(100, parseInt(scoreRange.value, 10) || 0));

  showAlert('info', 'Analyzing your assessment and computing skill gap...');
  submitBtn.disabled = true;

  const payload = {
    learnerName: learnerName,
    topicId: topicId,
    score: score
  };

  try {
    // authFetch automatically attaches Bearer token if logged in
    const response = await authFetch('/api/assessments', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || `Assessment could not be processed (Status: ${response.status})`);
    }

    // Render results on dashboard
    renderResult(data);

    showAlert('success', `Assessment analyzed successfully! Score: ${data.score}%, Target: ${data.targetScore}%, Skill Gap: ${data.gap} pts.`);

    // Refresh assessment history with current assessment highlighted
    await loadHistory(data.assessmentId);

    // Refresh student dashboard summary metrics if logged in
    if (isLoggedIn()) {
      await loadStudentDashboard();
    }
  } catch (error) {
    showAlert('danger', error.message || 'Unable to connect to SkillBridge AI backend.');
  } finally {
    submitBtn.disabled = false;
  }
});

// ==========================================
// 7. Result Dashboard Rendering
// ==========================================
function renderResult(data) {
  const score = Number(data.score);
  const target = Number(data.targetScore);
  const gap = Number(data.gap);
  const levelInfo = getLevelInfo(score);
  const gapWidth = Math.min(100 - score, gap);

  resultContainer.innerHTML = `
    <!-- Result Header Card -->
    <div class="result-header-card d-flex flex-wrap justify-content-between align-items-center gap-2">
      <div>
        <h3 class="h5 fw-bold mb-1 text-dark">${escapeHtml(data.topic)}</h3>
        <p class="text-secondary small mb-0">Competency evaluated for target career readiness</p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <span class="badge level-badge ${levelInfo.badgeClass} fs-6">${escapeHtml(data.level)}</span>
      </div>
    </div>

    <!-- 3 Metric Cards Grid -->
    <div class="result-grid">
      <div class="metric-card">
        <div class="metric-label"><i class="bi bi-speedometer2 text-primary"></i> Your Score</div>
        <div class="metric-value text-primary">${score}%</div>
        <div class="metric-hint">${levelInfo.name}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label"><i class="bi bi-bullseye text-dark"></i> Target Score</div>
        <div class="metric-value text-dark">${target}%</div>
        <div class="metric-hint">Industry Benchmark</div>
      </div>
      <div class="metric-card">
        <div class="metric-label"><i class="bi bi-arrows-expand text-warning"></i> Skill Gap</div>
        <div class="metric-value ${gap > 0 ? 'text-warning' : 'text-success'}">
          ${gap > 0 ? gap + ' <small class="fs-6 text-muted">pts</small>' : '<i class="bi bi-check-circle-fill text-success"></i> 0'}
        </div>
        <div class="metric-hint">${gap > 0 ? 'To bridge' : 'Target reached!'}</div>
      </div>
    </div>

    <!-- Multi-Segment Visual Progress Bar -->
    <div class="progress-card">
      <div class="d-flex justify-content-between align-items-center mb-1">
        <span class="fw-semibold small"><i class="bi bi-sliders2 text-primary me-1"></i> Proficiency & Gap Visualization</span>
        <span class="small text-muted">Baseline: 0% • Benchmark: ${target}%</span>
      </div>

      <div class="multi-progress-wrapper">
        <!-- Target Marker Line -->
        <div class="target-marker-line" style="left: ${target}%;">
          <div class="target-marker-label">Target (${target}%)</div>
        </div>

        <!-- Layered Progress Bar -->
        <div class="progress multi-progress" role="progressbar" aria-label="Skill score and gap">
          <div class="progress-bar ${levelInfo.barClass}" style="width: ${score}%;" title="Current Score: ${score}%">
            ${score >= 15 ? score + '%' : ''}
          </div>
          ${gap > 0 ? `
          <div class="progress-bar bar-gap progress-bar-striped progress-bar-animated text-dark fw-bold" 
               style="width: ${gapWidth}%;" title="Skill Gap: ${gap} pts">
            ${gapWidth >= 15 ? gap + ' pt gap' : ''}
          </div>` : ''}
        </div>
      </div>

      <!-- Legend -->
      <div class="progress-legend">
        <div class="legend-item">
          <span class="legend-swatch ${levelInfo.barClass}"></span>
          <span>Current Score (${score}%)</span>
        </div>
        ${gap > 0 ? `
        <div class="legend-item">
          <span class="legend-swatch bar-gap"></span>
          <span>Skill Gap (${gap} points)</span>
        </div>` : ''}
        <div class="legend-item">
          <span class="legend-swatch bg-dark"></span>
          <span>Target Benchmark (${target}%)</span>
        </div>
      </div>
    </div>

    <!-- AI Personalized Recommendation Box -->
    <div class="recommendation-card">
      <div class="recommendation-title">
        <i class="bi bi-lightbulb-fill text-warning fs-5"></i>
        <span>AI Personalized Recommendation</span>
      </div>
      <p class="recommendation-text">${escapeHtml(data.recommendation)}</p>
    </div>
  `;
}

// ==========================================
// 8. Assessment History Presentation
// ==========================================
async function loadHistory(highlightId = null) {
  const loggedIn = isLoggedIn();
  const user = getUser();
  const rawLearner = loggedIn ? user.fullName : (learnerInput.value.trim() || 'Demo Student');
  historySubtitle.textContent = `Past assessments for ${rawLearner}`;

  // Animate refresh icon
  refreshIcon.classList.add('spin-animation');

  try {
    let url = `/api/assessments`;
    if (!loggedIn) {
      url += `?learner=${encodeURIComponent(rawLearner)}`;
    }

    const response = await authFetch(url);
    if (!response.ok) throw new Error('Failed to retrieve history');
    const items = await response.json();

    if (!items.length) {
      historyContainer.innerHTML = `
        <div class="text-center py-4 text-muted">
          <i class="bi bi-inbox fs-3 d-block mb-2 text-secondary"></i>
          <p class="mb-0 small">No assessments recorded for <strong>${escapeHtml(rawLearner)}</strong> yet.</p>
        </div>`;
      return;
    }

    historyContainer.innerHTML = items.slice(0, 6).map(a => {
      const score = Number(a.score);
      const target = Number(a.targetScore);
      const gap = Math.max(0, target - score);
      const levelInfo = getLevelInfo(score);
      const isHighlighted = highlightId && a.id === highlightId;
      const formattedDate = formatDateTime(a.createdAt);

      return `
        <div class="history-item ${isHighlighted ? 'recent-highlight' : ''}">
          <div class="d-flex flex-wrap justify-content-between align-items-center gap-2">
            <div>
              <div class="d-flex align-items-center gap-2">
                <span class="fw-bold text-dark">${escapeHtml(a.topic.name)}</span>
                <span class="badge level-badge ${levelInfo.badgeClass}">${levelInfo.name}</span>
                ${isHighlighted ? '<span class="badge bg-primary-subtle text-primary">Just added</span>' : ''}
              </div>
              <div class="small text-muted mt-1">
                <span><i class="bi bi-briefcase me-1"></i>${escapeHtml(a.topic.careerRole)}</span>
                <span class="mx-1">•</span>
                <span><i class="bi bi-calendar3 me-1"></i>${formattedDate}</span>
              </div>
            </div>

            <div class="d-flex align-items-center gap-3">
              <div class="text-end d-none d-sm-block">
                <div class="small text-muted">Target: ${target}%</div>
                <div class="small fw-semibold ${gap > 0 ? 'text-warning' : 'text-success'}">
                  ${gap > 0 ? 'Gap: ' + gap + ' pts' : 'Goal met'}
                </div>
              </div>
              <div class="history-score-badge text-primary">
                ${score}%
              </div>
            </div>
          </div>

          <!-- Mini Progress Bar in History Card -->
          <div class="progress mini-progress mt-2 w-100" role="progressbar" aria-valuenow="${score}" aria-valuemin="0" aria-valuemax="100">
            <div class="progress-bar ${levelInfo.barClass}" style="width: ${score}%;"></div>
          </div>
        </div>
      `;
    }).join('');
  } catch (error) {
    historyContainer.innerHTML = `
      <div class="alert alert-warning py-2 small mb-0" role="alert">
        <i class="bi bi-exclamation-circle me-1"></i> Could not load assessment history.
      </div>`;
  } finally {
    setTimeout(() => refreshIcon.classList.remove('spin-animation'), 400);
  }
}

// Refresh button listener
refreshBtn.addEventListener('click', () => loadHistory());

// Auto-reload history when learner name is changed in unauthenticated mode
learnerInput.addEventListener('input', () => {
  if (isLoggedIn()) return;
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    loadHistory();
  }, 500);
});

// ==========================================
// 9. Helper Functions
// ==========================================
function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, c => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[c]));
}

function formatDateTime(isoString) {
  if (!isoString) return 'Recent';
  try {
    const d = new Date(isoString);
    if (isNaN(d.getTime())) return 'Recent';
    return d.toLocaleDateString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return 'Recent';
  }
}

// ==========================================
// 10. Startup Initialization
// ==========================================
initAuthState();
setScore(55);
loadTopics();
loadHistory();
