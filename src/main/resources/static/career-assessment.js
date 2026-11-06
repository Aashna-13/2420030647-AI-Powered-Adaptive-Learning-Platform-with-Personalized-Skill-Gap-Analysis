// SkillBridge AI - Phase 3: Career-Based Skill Assessment & Adaptive Analysis
// Client-side Controller, Quiz Engine, and Chart.js Gap Visualizer

let careerCatalog = [];
let activeQuizState = {
  career: null,
  questions: [],
  currentIndex: 0,
  answers: {} // { [questionId]: selectedOptionIndex (0..3) }
};
let radarChartInstance = null;

// ==========================================
// 1. Initialization & Tab Management
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
  initFeatureTabs();
  loadCareerCatalog();
  if (isLoggedIn()) {
    loadCareerHistory();
  }
});

function initFeatureTabs() {
  const tabCareer = document.getElementById('tabCareerAssessment');
  const tabQuick = document.getElementById('tabQuickBenchmark');
  const viewCareer = document.getElementById('careerAssessmentView');
  const viewQuick = document.getElementById('quickBenchmarkView');

  if (tabCareer && tabQuick && viewCareer && viewQuick) {
    tabCareer.addEventListener('click', (e) => {
      e.preventDefault();
      tabCareer.classList.add('active');
      tabQuick.classList.remove('active');
      viewCareer.classList.remove('d-none');
      viewQuick.classList.add('d-none');
    });

    tabQuick.addEventListener('click', (e) => {
      e.preventDefault();
      tabQuick.classList.add('active');
      tabCareer.classList.remove('active');
      viewQuick.classList.remove('d-none');
      viewCareer.classList.add('d-none');
    });
  }
}

// ==========================================
// 2. Career Catalog Loader & Card Renderer
// ==========================================
async function loadCareerCatalog() {
  const container = document.getElementById('careerCardsGrid');
  if (!container) return;

  try {
    const res = await fetch('/api/careers');
    if (!res.ok) throw new Error('Failed to load career catalog');
    careerCatalog = await res.json();

    renderCareerCards(careerCatalog);
  } catch (err) {
    console.error('Error loading careers:', err);
    container.innerHTML = `
      <div class="col-12 text-center py-4 text-danger">
        <i class="bi bi-exclamation-triangle fs-3 d-block mb-2"></i>
        <span>Unable to load career paths. Please check your connection and refresh.</span>
      </div>
    `;
  }
}

function renderCareerCards(careers) {
  const container = document.getElementById('careerCardsGrid');
  if (!container) return;

  if (!careers || careers.length === 0) {
    container.innerHTML = `<div class="col-12 text-center text-muted py-4">No career paths available.</div>`;
    return;
  }

  container.innerHTML = careers.map(c => {
    const badgeColor = c.badgeColor || 'primary';
    const iconClass = c.icon || 'bi-briefcase';
    const skillsHtml = (c.requiredSkills || []).map(s => `<span class="skill-tag">${escapeHtml(s)}</span>`).join('');

    return `
      <div class="col-12 col-md-6 col-xl-4">
        <div class="career-card">
          <div class="career-card-header">
            <div class="career-icon-box career-icon-${badgeColor}">
              <i class="bi ${escapeHtml(iconClass)}"></i>
            </div>
            <span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle px-3 py-2 fw-semibold">
              <i class="bi bi-bullseye me-1"></i> Target: ${c.targetProficiency}%
            </span>
          </div>

          <h3 class="career-title">${escapeHtml(c.title)}</h3>
          <p class="career-description">${escapeHtml(c.description)}</p>

          <div class="mb-2">
            <small class="text-uppercase fw-bold text-muted" style="font-size: 0.72rem; letter-spacing: 0.5px;">REQUIRED COMPETENCIES</small>
          </div>
          <div class="career-skill-tags">
            ${skillsHtml}
          </div>

          <div class="mt-auto pt-3 border-top d-flex justify-content-between align-items-center">
            <span class="small text-muted fw-medium">
              <i class="bi bi-patch-question me-1 text-primary"></i> ${c.totalQuestions || 30} Questions
            </span>
            <button class="btn btn-primary btn-sm px-3 py-2 fw-semibold rounded-3 btn-start-quiz" data-career-id="${escapeHtml(c.id)}">
              <i class="bi bi-play-circle-fill me-1"></i> Start Assessment
            </button>
          </div>
        </div>
      </div>
    `;
  }).join('');

  // Attach click listeners to all Start Assessment buttons
  container.querySelectorAll('.btn-start-quiz').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const careerId = e.currentTarget.getAttribute('data-career-id');
      startQuiz(careerId);
    });
  });
}

// ==========================================
// 3. Quiz State & Engine
// ==========================================
async function startQuiz(careerId) {
  const career = careerCatalog.find(c => c.id.toLowerCase() === careerId.toLowerCase());
  if (!career) return;

  // Show loading indicator
  const selectionSec = document.getElementById('careerSelectionSection');
  const quizSec = document.getElementById('careerQuizSection');
  const resultSec = document.getElementById('careerResultSection');

  selectionSec.classList.add('d-none');
  resultSec.classList.add('d-none');
  quizSec.classList.remove('d-none');

  quizSec.innerHTML = `
    <div class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-secondary">Loading questions for ${escapeHtml(career.title)}...</p>
    </div>
  `;

  try {
    const res = await authFetch(`/api/careers/${careerId}/adaptive-questions`);
    if (!res.ok) throw new Error('Failed to load questions');
    const questions = await res.json();

    if (!questions || questions.length === 0) {
      throw new Error('No questions available for this career');
    }

    activeQuizState = {
      career: career,
      questions: questions,
      currentIndex: 0,
      answers: {}
    };

    renderQuizInterface();
  } catch (err) {
    console.error('Quiz start error:', err);
    quizSec.innerHTML = `
      <div class="alert alert-danger p-4 text-center">
        <i class="bi bi-exclamation-triangle-fill fs-3 d-block mb-2"></i>
        <h5>Failed to initialize assessment</h5>
        <p class="small text-muted mb-3">${escapeHtml(err.message)}</p>
        <button class="btn btn-outline-danger btn-sm" onclick="cancelQuiz()">Return to Career Selection</button>
      </div>
    `;
  }
}

function cancelQuiz() {
  const selectionSec = document.getElementById('careerSelectionSection');
  const quizSec = document.getElementById('careerQuizSection');
  const resultSec = document.getElementById('careerResultSection');

  quizSec.classList.add('d-none');
  resultSec.classList.add('d-none');
  selectionSec.classList.remove('d-none');

  activeQuizState = { career: null, questions: [], currentIndex: 0, answers: {} };
}

function renderQuizInterface() {
  const quizSec = document.getElementById('careerQuizSection');
  const { career, questions } = activeQuizState;

  quizSec.innerHTML = `
    <div class="quiz-panel">
      <!-- Quiz Top Navigation & Info -->
      <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3">
        <div class="d-flex align-items-center gap-2">
          <button class="btn btn-outline-secondary btn-sm" id="btnCancelQuiz" title="Back to careers">
            <i class="bi bi-arrow-left me-1"></i> Exit Quiz
          </button>
          <div class="ms-2">
            <h3 class="h5 fw-bold mb-0 text-dark">${escapeHtml(career.title)} Assessment</h3>
            <span class="text-secondary small">Adaptive Career Competency Evaluation</span>
          </div>
        </div>
        <div class="d-flex align-items-center gap-3">
          <div class="text-end">
            <span class="small text-muted d-block">Progress</span>
            <span class="fw-bold text-dark" id="quizAnsweredCount">0 / ${questions.length} Answered</span>
          </div>
        </div>
      </div>

      <!-- Progress Track -->
      <div class="quiz-progress-track mb-4">
        <div class="quiz-progress-fill" id="quizProgressFill" style="width: 0%;"></div>
      </div>

      <div class="row g-4">
        <!-- Main Quiz Question Column -->
        <div class="col-12 col-lg-8">
          <div class="quiz-question-box shadow-sm" id="quizQuestionContainer">
            <!-- Injected dynamically by renderCurrentQuestion() -->
          </div>

          <!-- Bottom Navigation Controls -->
          <div class="d-flex justify-content-between align-items-center mt-3">
            <button class="btn btn-outline-secondary px-4 py-2" id="btnPrevQuestion">
              <i class="bi bi-chevron-left me-1"></i> Previous
            </button>
            <button class="btn btn-primary px-4 py-2" id="btnNextQuestion">
              Next <i class="bi bi-chevron-right ms-1"></i>
            </button>
          </div>
        </div>

        <!-- Right Side: Question Palette & Submission -->
        <div class="col-12 col-lg-4">
          <div class="p-3 bg-light rounded-4 border">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <span class="fw-bold small text-dark">Question Navigator</span>
              <span class="badge bg-white text-secondary border small" id="paletteSummaryBadge">30 Questions</span>
            </div>
            <p class="small text-muted mb-3">Click any question number to jump directly to it.</p>

            <!-- Question Number Palette Grid -->
            <div class="quiz-palette mb-3" id="quizPaletteContainer">
              ${questions.map((q, idx) => `
                <button type="button" class="palette-btn" data-index="${idx}" id="paletteBtn_${idx}">
                  ${idx + 1}
                </button>
              `).join('')}
            </div>

            <!-- Pre-Submission Legend -->
            <div class="d-flex justify-content-around text-muted small border-top pt-2 mb-3" style="font-size: 0.78rem;">
              <span><span class="d-inline-block rounded-circle bg-primary me-1" style="width: 8px; height: 8px;"></span> Answered</span>
              <span><span class="d-inline-block rounded-circle bg-white border me-1" style="width: 8px; height: 8px;"></span> Unanswered</span>
              <span><span class="d-inline-block rounded-circle bg-dark me-1" style="width: 8px; height: 8px;"></span> Current</span>
            </div>

            <!-- Submit Assessment Button -->
            <button class="btn btn-success w-100 py-3 fw-bold shadow-sm" id="btnSubmitQuiz">
              <i class="bi bi-check2-circle me-1"></i> Submit Assessment
            </button>
            <div id="quizValidationWarning" class="alert alert-warning small mt-2 mb-0 d-none" role="alert"></div>
          </div>
        </div>
      </div>
    </div>
  `;

  // Attach Event Handlers
  document.getElementById('btnCancelQuiz').addEventListener('click', cancelQuiz);
  document.getElementById('btnPrevQuestion').addEventListener('click', prevQuestion);
  document.getElementById('btnNextQuestion').addEventListener('click', nextQuestion);
  document.getElementById('btnSubmitQuiz').addEventListener('click', submitQuiz);

  quizSec.querySelectorAll('.palette-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const idx = parseInt(e.currentTarget.getAttribute('data-index'), 10);
      goToQuestion(idx);
    });
  });

  renderCurrentQuestion();
}

function renderCurrentQuestion() {
  const { questions, currentIndex, answers } = activeQuizState;
  const q = questions[currentIndex];
  if (!q) return;

  const container = document.getElementById('quizQuestionContainer');
  const answeredCount = Object.keys(answers).length;
  const total = questions.length;
  const progressPercent = Math.round((answeredCount / total) * 100);

  // Update Progress
  const progressFill = document.getElementById('quizProgressFill');
  if (progressFill) progressFill.style.width = `${progressPercent}%`;

  const countDisplay = document.getElementById('quizAnsweredCount');
  if (countDisplay) countDisplay.textContent = `${answeredCount} / ${total} Answered`;

  // Update Palette active and answered styling
  questions.forEach((item, idx) => {
    const pBtn = document.getElementById(`paletteBtn_${idx}`);
    if (pBtn) {
      pBtn.classList.remove('active');
      if (answers[item.id] !== undefined) {
        pBtn.classList.add('answered');
      } else {
        pBtn.classList.remove('answered');
      }
      if (idx === currentIndex) {
        pBtn.classList.add('active');
      }
    }
  });

  // Determine difficulty chip color
  let diffBadgeClass = 'bg-secondary';
  if (q.difficulty === 'BEGINNER') diffBadgeClass = 'bg-success-subtle text-success border border-success-subtle';
  else if (q.difficulty === 'INTERMEDIATE') diffBadgeClass = 'bg-primary-subtle text-primary border border-primary-subtle';
  else if (q.difficulty === 'ADVANCED') diffBadgeClass = 'bg-danger-subtle text-danger border border-danger-subtle';

  const selectedAnswer = answers[q.id];

  const optionsHtml = (q.options || []).map((opt, optIdx) => {
    const isChecked = selectedAnswer === optIdx;
    const selectedClass = isChecked ? 'selected' : '';
    const optionLetter = String.fromCharCode(65 + optIdx); // 'A', 'B', 'C', 'D'

    return `
      <div class="quiz-option-card ${selectedClass}" data-option-index="${optIdx}">
        <input class="quiz-option-radio" type="radio" name="quizQuestionOption" id="opt_${optIdx}" value="${optIdx}" ${isChecked ? 'checked' : ''}>
        <label class="quiz-option-text mb-0" for="opt_${optIdx}">
          <strong class="me-2 text-primary">${optionLetter}.</strong> ${escapeHtml(opt)}
        </label>
      </div>
    `;
  }).join('');

  container.innerHTML = `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div class="d-flex align-items-center gap-2">
        <span class="badge bg-primary text-white px-3 py-2 fw-semibold">
          Question ${currentIndex + 1} of ${total}
        </span>
        <span class="badge bg-light text-dark border px-3 py-2 fw-medium">
          <i class="bi bi-tag me-1"></i> ${escapeHtml(q.skillCategory)}
        </span>
      </div>
      <span class="badge rounded-pill ${diffBadgeClass} px-3 py-2 text-uppercase fw-semibold" style="font-size: 0.75rem;">
        ${escapeHtml(q.difficulty)}
      </span>
    </div>

    <h4 class="h5 fw-bold text-dark my-4 lh-base">${escapeHtml(q.questionText)}</h4>

    <div class="quiz-options-list mb-2">
      ${optionsHtml}
    </div>
  `;

  // Attach option click listeners
  container.querySelectorAll('.quiz-option-card').forEach(card => {
    card.addEventListener('click', (e) => {
      const optIdx = parseInt(card.getAttribute('data-option-index'), 10);
      selectOption(q.id, optIdx);
    });
  });

  // Update Prev / Next button states
  const btnPrev = document.getElementById('btnPrevQuestion');
  const btnNext = document.getElementById('btnNextQuestion');

  if (btnPrev) btnPrev.disabled = (currentIndex === 0);
  if (btnNext) {
    if (currentIndex === total - 1) {
      btnNext.innerHTML = `Finish Review <i class="bi bi-check-circle ms-1"></i>`;
    } else {
      btnNext.innerHTML = `Next <i class="bi bi-chevron-right ms-1"></i>`;
    }
  }
}

function selectOption(questionId, optionIndex) {
  activeQuizState.answers[questionId] = optionIndex;
  renderCurrentQuestion();
}

function prevQuestion() {
  if (activeQuizState.currentIndex > 0) {
    activeQuizState.currentIndex--;
    renderCurrentQuestion();
  }
}

function nextQuestion() {
  if (activeQuizState.currentIndex < activeQuizState.questions.length - 1) {
    activeQuizState.currentIndex++;
    renderCurrentQuestion();
  }
}

function goToQuestion(index) {
  if (index >= 0 && index < activeQuizState.questions.length) {
    activeQuizState.currentIndex = index;
    renderCurrentQuestion();
  }
}

// ==========================================
// 4. Assessment Submission & Scoring
// ==========================================
async function submitQuiz() {
  const { career, questions, answers } = activeQuizState;
  const btnSubmit = document.getElementById('btnSubmitQuiz');
  const warningBox = document.getElementById('quizValidationWarning');

  warningBox.classList.add('d-none');
  warningBox.textContent = '';

  // Check login state: Career assessment persistence requires student auth
  if (!isLoggedIn()) {
    warningBox.innerHTML = `
      <i class="bi bi-shield-lock me-1"></i>
      Please <a href="/login.html" class="fw-bold alert-link">sign in to your student account</a> or <a href="/register.html" class="fw-bold alert-link">register</a> to submit and record your career assessment.
    `;
    warningBox.classList.remove('d-none');
    return;
  }

  // Validate that all questions are answered
  const total = questions.length;
  const answeredCount = Object.keys(answers).length;
  const unansweredIndices = [];

  questions.forEach((q, idx) => {
    if (answers[q.id] === undefined) {
      unansweredIndices.push(idx + 1);
    }
  });

  if (unansweredIndices.length > 0) {
    warningBox.innerHTML = `
      <i class="bi bi-exclamation-circle-fill me-1"></i>
      <strong>${unansweredIndices.length} unanswered question(s) remaining!</strong><br>
      Please answer all questions before submitting. Unanswered: #${unansweredIndices.join(', #')}
    `;
    warningBox.classList.remove('d-none');
    // Jump to the first unanswered question
    goToQuestion(unansweredIndices[0] - 1);
    return;
  }

  // Confirm submission
  if (!confirm(`Are you ready to submit your ${career.title} assessment? All ${total} questions are answered.`)) {
    return;
  }

  // Prevent duplicate submission: disable button & show spinner
  btnSubmit.disabled = true;
  btnSubmit.innerHTML = `<span class="spinner-border spinner-border-sm me-2" role="status"></span> Scoring Assessment...`;

  try {
    const payload = {
      answers: answers
    };

    const response = await authFetch(`/api/careers/${career.id}/assessments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Failed to submit assessment');
    }

    const result = await response.json();

    // Display Result View
    renderCareerResult(result);

    // Refresh history
    loadCareerHistory();
  } catch (err) {
    console.error('Submission error:', err);
    warningBox.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-1"></i> ${escapeHtml(err.message)}`;
    warningBox.classList.remove('d-none');
    btnSubmit.disabled = false;
    btnSubmit.innerHTML = `<i class="bi bi-check2-circle me-1"></i> Submit Assessment`;
  }
}

// ==========================================
// 5. Results & Skill Gap Visualizer
// ==========================================
function renderCareerResult(res) {
  const selectionSec = document.getElementById('careerSelectionSection');
  const quizSec = document.getElementById('careerQuizSection');
  const resultSec = document.getElementById('careerResultSection');

  selectionSec.classList.add('d-none');
  quizSec.classList.add('d-none');
  resultSec.classList.remove('d-none');

  // Format date
  const dateFormatted = res.createdAt ? new Date(res.createdAt).toLocaleString(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short'
  }) : 'Just now';

  // Overall readiness color indicator
  let readinessColor = '#10b981'; // Green (>= 80)
  let readinessBadge = 'Strong Readiness';
  if (res.readinessPercentage < 40) {
    readinessColor = '#ef4444'; // Red
    readinessBadge = 'Needs Improvement';
  } else if (res.readinessPercentage < 60) {
    readinessColor = '#f59e0b'; // Amber
    readinessBadge = 'Developing';
  } else if (res.readinessPercentage < 80) {
    readinessColor = '#0284c7'; // Blue
    readinessBadge = 'Intermediate';
  }

  // Render Skill Breakdown Cards
  const breakdownHtml = (res.skillBreakdown || []).map(skill => {
    let levelBadgeClass = 'level-strong';
    let barColorClass = 'bar-strong';

    if (skill.level === 'Needs Improvement') {
      levelBadgeClass = 'level-needs-improvement';
      barColorClass = 'bar-needs-improvement';
    } else if (skill.level === 'Developing') {
      levelBadgeClass = 'level-developing';
      barColorClass = 'bar-developing';
    } else if (skill.level === 'Intermediate') {
      levelBadgeClass = 'level-intermediate';
      barColorClass = 'bar-intermediate';
    }

    const gapText = skill.gap === 0 ? '<span class="text-success fw-bold"><i class="bi bi-check-circle me-1"></i>Target Met (0 pts)</span>'
                                    : `<span class="text-warning fw-bold">Gap: ${skill.gap} pts</span>`;

    return `
      <div class="col-12 col-md-6">
        <div class="skill-breakdown-card h-100 d-flex flex-column">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h4 class="h6 fw-bold mb-0 text-dark">${escapeHtml(skill.skillName)}</h4>
            <span class="badge level-badge ${levelBadgeClass} px-3 py-1 fs-6">${escapeHtml(skill.level)}</span>
          </div>

          <div class="d-flex justify-content-between align-items-center small text-muted mb-2">
            <span>Score: <strong class="text-dark">${skill.score}%</strong> (${skill.correctAnswers}/${skill.totalQuestions} correct)</span>
            <span>${gapText}</span>
          </div>

          <!-- Progress bar with 80% benchmark target marker -->
          <div class="position-relative mt-auto">
            <div class="progress" style="height: 12px; border-radius: 6px; background-color: #e2e8f0;">
              <div class="progress-bar ${barColorClass}" role="progressbar" style="width: ${skill.score}%;" aria-valuenow="${skill.score}" aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <!-- Target line at 80% -->
            <div class="target-benchmark-marker" style="left: ${skill.targetScore}%;" title="Career Target Benchmark (${skill.targetScore}%)"></div>
          </div>
        </div>
      </div>
    `;
  }).join('');

  // Render Tailored Recommendations
  const recsHtml = (res.recommendations || []).map(rec => `
    <li class="list-group-item d-flex align-items-start gap-3 py-3 border-0 border-bottom">
      <div class="icon-circle bg-primary-subtle text-primary flex-shrink-0 mt-1" style="width: 32px; height: 32px; border-radius: 8px;">
        <i class="bi bi-arrow-right-short fs-4"></i>
      </div>
      <div>
        <p class="mb-0 text-dark" style="font-size: 0.94rem;">${escapeHtml(rec)}</p>
      </div>
    </li>
  `).join('');

  resultSec.innerHTML = `
    <!-- Top Action Row -->
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
      <div>
        <button class="btn btn-outline-secondary btn-sm mb-2" id="btnBackToCareers">
          <i class="bi bi-grid me-1"></i> All Careers
        </button>
        <h2 class="h3 fw-bold mb-1 text-dark">${escapeHtml(res.careerTitle)} Analysis</h2>
        <span class="text-secondary small">Evaluated for <strong>${escapeHtml(res.studentName)}</strong> • ${dateFormatted}</span>
      </div>
      <div class="d-flex gap-2 flex-wrap">
        <button class="btn btn-success btn-sm px-3" id="btnViewLmsRoadmap">
          <i class="bi bi-signpost-split-fill me-1"></i> View Learning Roadmap & Courses
        </button>
        <button class="btn btn-outline-primary btn-sm px-3" onclick="window.print()">
          <i class="bi bi-printer me-1"></i> Print Report
        </button>
        <button class="btn btn-primary btn-sm px-3" id="btnRetakeCareerQuiz">
          <i class="bi bi-arrow-clockwise me-1"></i> Retake Assessment
        </button>
      </div>
    </div>

    <!-- Hero Readiness & Metric Cards -->
    <div class="readiness-hero-card mb-4">
      <div class="row align-items-center g-4">
        <!-- Circular Score Ring -->
        <div class="col-12 col-md-4 text-center">
          <div class="readiness-score-circle mx-auto mb-2" style="border-color: ${readinessColor};">
            <span class="readiness-score-number" style="color: #ffffff;">${res.readinessPercentage}%</span>
            <small class="text-white-50 text-uppercase fw-semibold" style="font-size: 0.72rem;">Readiness</small>
          </div>
          <span class="badge rounded-pill px-3 py-2 fw-semibold" style="background: rgba(255,255,255,0.15); color: #ffffff;">
            ${readinessBadge}
          </span>
        </div>

        <!-- Metrics Breakdown Grid -->
        <div class="col-12 col-md-8">
          <div class="row g-3">
            <div class="col-6 col-sm-3">
              <div class="p-3 rounded-3 bg-white bg-opacity-10 text-center">
                <small class="text-white-50 d-block mb-1">SCORE</small>
                <strong class="fs-4 text-white">${res.overallScore}%</strong>
              </div>
            </div>
            <div class="col-6 col-sm-3">
              <div class="p-3 rounded-3 bg-white bg-opacity-10 text-center">
                <small class="text-white-50 d-block mb-1">CORRECT</small>
                <strong class="fs-4 text-white">${res.correctAnswers} / ${res.totalQuestions}</strong>
              </div>
            </div>
            <div class="col-6 col-sm-3">
              <div class="p-3 rounded-3 bg-white bg-opacity-10 text-center">
                <small class="text-white-50 d-block mb-1">BENCHMARK</small>
                <strong class="fs-4 text-white">${res.targetProficiency}%</strong>
              </div>
            </div>
            <div class="col-6 col-sm-3">
              <div class="p-3 rounded-3 bg-white bg-opacity-10 text-center">
                <small class="text-white-50 d-block mb-1">TOTAL GAP</small>
                <strong class="fs-4 text-warning">${res.totalSkillGap} pts</strong>
              </div>
            </div>
          </div>

          <!-- Strongest & Weakest Callout -->
          <div class="row g-3 mt-1">
            <div class="col-12 col-sm-6">
              <div class="p-3 rounded-3 bg-success bg-opacity-20 border border-success border-opacity-25 d-flex align-items-center gap-3">
                <i class="bi bi-trophy-fill text-success fs-3"></i>
                <div>
                  <small class="text-success-emphasis d-block fw-semibold" style="font-size: 0.75rem;">STRONGEST COMPETENCY</small>
                  <strong class="text-white">${escapeHtml(res.strongestSkill)}</strong>
                </div>
              </div>
            </div>
            <div class="col-12 col-sm-6">
              <div class="p-3 rounded-3 bg-warning bg-opacity-20 border border-warning border-opacity-25 d-flex align-items-center gap-3">
                <i class="bi bi-bullseye text-warning fs-3"></i>
                <div>
                  <small class="text-warning-emphasis d-block fw-semibold" style="font-size: 0.75rem;">PRIMARY FOCUS AREA</small>
                  <strong class="text-white">${escapeHtml(res.weakestSkill)}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Chart & Skill Breakdown Section -->
    <div class="row g-4 mb-4">
      <!-- Radar Chart -->
      <div class="col-12 col-lg-5">
        <div class="panel h-100 d-flex flex-column">
          <h3 class="h5 fw-bold text-dark mb-1">Competency Benchmark Chart</h3>
          <p class="text-muted small mb-3">Comparing your scores vs the 80% industry career benchmark.</p>
          <div class="flex-grow-1 position-relative" style="min-height: 280px;">
            <canvas id="careerRadarChart"></canvas>
          </div>
        </div>
      </div>

      <!-- Skill-Wise Breakdown Cards -->
      <div class="col-12 col-lg-7">
        <div class="panel h-100">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
              <h3 class="h5 fw-bold text-dark mb-0">Skill-Wise Analysis</h3>
              <span class="text-muted small">Target milestone: 80% for each skill</span>
            </div>
            <span class="badge bg-light text-secondary border">
              <i class="bi bi-bar-chart-steps me-1"></i> ${res.skillBreakdown ? res.skillBreakdown.length : 0} Skills
            </span>
          </div>

          <div class="row g-3">
            ${breakdownHtml}
          </div>
        </div>
      </div>
    </div>

    <!-- Tailored Recommendations -->
    <div class="panel mb-4">
      <div class="d-flex align-items-center gap-2 mb-3">
        <div class="icon-circle bg-warning-subtle text-warning">
          <i class="bi bi-compass-fill"></i>
        </div>
        <div>
          <h3 class="h5 fw-bold text-dark mb-0">Recommended Next Learning Steps</h3>
          <p class="text-muted small mb-0">Actionable adaptive steps prioritized by your highest proficiency gaps.</p>
        </div>
      </div>
      <ul class="list-group list-group-flush">
        ${recsHtml}
      </ul>
    </div>

    <!-- Required Career Readiness Disclaimer -->
    <div class="disclaimer-banner mb-4">
      <div class="d-flex align-items-start gap-2">
        <i class="bi bi-info-circle-fill text-secondary fs-5 mt-1"></i>
        <div>
          <strong class="d-block text-dark">Assessment Disclaimer:</strong>
          <span>${escapeHtml(res.disclaimer || 'The career readiness score is an estimate based on the assessment, not a guaranteed measure of employability.')}</span>
        </div>
      </div>
    </div>
  `;

  // Attach button event listeners
  document.getElementById('btnBackToCareers').addEventListener('click', cancelQuiz);
  const btnRoadmap = document.getElementById('btnViewLmsRoadmap');
  if (btnRoadmap) {
    btnRoadmap.addEventListener('click', () => {
      const tab = document.getElementById('tabLearningRoadmap');
      if (tab) tab.click();
    });
  }
  document.getElementById('btnRetakeCareerQuiz').addEventListener('click', () => {
    startQuiz(res.careerId);
  });

  // Render Radar Chart with Chart.js
  renderRadarChart(res.skillBreakdown || []);

  // Smooth scroll to results
  resultSec.scrollIntoView({ behavior: 'smooth' });
}

function renderRadarChart(skills) {
  const canvas = document.getElementById('careerRadarChart');
  if (!canvas || typeof Chart === 'undefined') return;

  if (radarChartInstance) {
    radarChartInstance.destroy();
  }

  const labels = skills.map(s => s.skillName);
  const studentScores = skills.map(s => s.score);
  const targetBenchmarks = skills.map(() => 80);

  const ctx = canvas.getContext('2d');
  radarChartInstance = new Chart(ctx, {
    type: 'radar',
    data: {
      labels: labels,
      datasets: [
        {
          label: 'Your Score (%)',
          data: studentScores,
          backgroundColor: 'rgba(79, 101, 241, 0.25)',
          borderColor: '#4f65f1',
          pointBackgroundColor: '#4f65f1',
          pointBorderColor: '#ffffff',
          pointHoverBackgroundColor: '#ffffff',
          pointHoverBorderColor: '#4f65f1',
          borderWidth: 2
        },
        {
          label: 'Target Benchmark (80%)',
          data: targetBenchmarks,
          backgroundColor: 'rgba(16, 185, 129, 0.08)',
          borderColor: '#10b981',
          borderDash: [4, 4],
          pointBackgroundColor: '#10b981',
          pointBorderColor: '#ffffff',
          borderWidth: 2
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        r: {
          min: 0,
          max: 100,
          ticks: {
            stepSize: 20,
            showLabelBackdrop: false,
            font: { size: 10 }
          },
          pointLabels: {
            font: { size: 11, weight: '600' },
            color: '#334155'
          },
          grid: {
            color: '#e2e8f0'
          },
          angleLines: {
            color: '#e2e8f0'
          }
        }
      },
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            boxWidth: 12,
            font: { size: 12 }
          }
        }
      }
    }
  });
}

// ==========================================
// 6. Career Assessment History
// ==========================================
async function loadCareerHistory() {
  const container = document.getElementById('careerHistoryContainer');
  if (!container) return;

  try {
    const res = await authFetch('/api/careers/assessments/my');
    if (!res.ok) return;
    const history = await res.json();

    if (!history || history.length === 0) {
      container.innerHTML = `
        <div class="text-center py-4 text-muted small">
          <i class="bi bi-clock-history fs-3 d-block mb-1 text-secondary"></i>
          <span>No career assessments recorded yet. Select a career path above to begin.</span>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light small">
            <tr>
              <th scope="col">Date</th>
              <th scope="col">Career Path</th>
              <th scope="col">Readiness</th>
              <th scope="col">Score</th>
              <th scope="col">Strongest Skill</th>
              <th scope="col" class="text-end">Action</th>
            </tr>
          </thead>
          <tbody>
            ${history.map(item => {
              const dt = item.createdAt ? new Date(item.createdAt).toLocaleDateString(undefined, {
                month: 'short', day: 'numeric', year: 'numeric'
              }) : 'N/A';

              let readBadge = 'level-strong';
              if (item.readinessPercentage < 40) readBadge = 'level-needs-improvement';
              else if (item.readinessPercentage < 60) readBadge = 'level-developing';
              else if (item.readinessPercentage < 80) readBadge = 'level-intermediate';

              return `
                <tr>
                  <td class="small text-muted">${dt}</td>
                  <td><strong>${escapeHtml(item.careerTitle)}</strong></td>
                  <td><span class="badge level-badge ${readBadge} px-2 py-1">${item.readinessPercentage}%</span></td>
                  <td class="small text-secondary">${item.overallScore}% (${item.correctAnswers}/${item.totalQuestions})</td>
                  <td class="small text-truncate" style="max-width: 160px;">${escapeHtml(item.strongestSkill)}</td>
                  <td class="text-end">
                    <button class="btn btn-outline-primary btn-sm px-2 py-1 btn-view-history" data-history-id="${item.assessmentId}">
                      <i class="bi bi-eye me-1"></i> View
                    </button>
                  </td>
                </tr>
              `;
            }).join('')}
          </tbody>
        </table>
      </div>
    `;

    // Attach click listeners to view buttons
    container.querySelectorAll('.btn-view-history').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const id = e.currentTarget.getAttribute('data-history-id');
        try {
          const detailRes = await authFetch(`/api/careers/assessments/${id}`);
          if (detailRes.ok) {
            const detailData = await detailRes.json();
            renderCareerResult(detailData);
          }
        } catch (err) {
          console.error('Error fetching assessment detail:', err);
        }
      });
    });
  } catch (err) {
    console.error('Error loading career history:', err);
  }
}
