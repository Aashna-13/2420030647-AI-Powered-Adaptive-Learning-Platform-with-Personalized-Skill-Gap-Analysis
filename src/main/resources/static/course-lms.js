// SkillBridge AI - Learning Management System & Theory Courses Module

let currentCourses = [];
let currentFilterCareer = 'ALL';
let currentFilterSkill = 'ALL';
let currentFilterDifficulty = 'ALL';
let currentSearchQuery = '';
let currentLessonData = null;
let currentQuizSubmission = null;

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  initLmsNavigation();
  initLmsFilters();
  loadCourseCatalog();
});

// =========================================================================
// 1. NAVIGATION & VIEW SWITCHING
// =========================================================================

function initLmsNavigation() {
  const tabCourseCatalog = document.getElementById('tabCourseCatalog');
  const tabMyCourses = document.getElementById('tabMyCourses');
  const tabLearningRoadmap = document.getElementById('tabLearningRoadmap');
  const tabCareerAssessment = document.getElementById('tabCareerAssessment');
  const tabQuickBenchmark = document.getElementById('tabQuickBenchmark');
  const tabCourseAdmin = document.getElementById('tabCourseAdmin');

  if (tabCourseCatalog) {
    tabCourseCatalog.addEventListener('click', () => switchLmsView('catalog'));
  }
  if (tabMyCourses) {
    tabMyCourses.addEventListener('click', () => {
      if (!isLoggedIn()) {
        window.location.href = '/login.html?redirect=my-courses';
        return;
      }
      switchLmsView('my-courses');
      loadMyCourses();
    });
  }
  if (tabLearningRoadmap) {
    tabLearningRoadmap.addEventListener('click', () => {
      if (!isLoggedIn()) {
        window.location.href = '/login.html?redirect=roadmap';
        return;
      }
      switchLmsView('roadmap');
      loadLearningRoadmap();
    });
  }
  if (tabCourseAdmin) {
    tabCourseAdmin.addEventListener('click', () => {
      const user = getUser();
      if (!user || user.role !== 'ADMIN') {
        alert('Administrator access required.');
        return;
      }
      switchLmsView('admin');
      loadAdminCourses();
    });
  }

  // Adjust existing tabs to switch away from LMS views cleanly
  if (tabCareerAssessment) {
    const origHandler = tabCareerAssessment.onclick;
    tabCareerAssessment.addEventListener('click', () => {
      hideAllLmsViews();
      document.getElementById('careerAssessmentView').classList.remove('d-none');
      document.getElementById('quickBenchmarkView').classList.add('d-none');
      setActiveFeatureTab(tabCareerAssessment);
    });
  }
  if (tabQuickBenchmark) {
    tabQuickBenchmark.addEventListener('click', () => {
      hideAllLmsViews();
      document.getElementById('quickBenchmarkView').classList.remove('d-none');
      document.getElementById('careerAssessmentView').classList.add('d-none');
      setActiveFeatureTab(tabQuickBenchmark);
    });
  }

  // Check admin role to show admin tab
  updateAdminTabVisibility();
}

function updateAdminTabVisibility() {
  const adminTab = document.getElementById('tabCourseAdminItem');
  if (!adminTab) return;
  const user = getUser();
  if (user && user.role === 'ADMIN') {
    adminTab.classList.remove('d-none');
  } else {
    adminTab.classList.add('d-none');
  }
}

function switchLmsView(viewName) {
  hideAllLmsViews();
  const careerView = document.getElementById('careerAssessmentView');
  const quickView = document.getElementById('quickBenchmarkView');
  if (careerView) careerView.classList.add('d-none');
  if (quickView) quickView.classList.add('d-none');

  if (viewName === 'catalog') {
    document.getElementById('courseCatalogView').classList.remove('d-none');
    setActiveFeatureTab(document.getElementById('tabCourseCatalog'));
    loadCourseCatalog();
  } else if (viewName === 'my-courses') {
    document.getElementById('myCoursesView').classList.remove('d-none');
    setActiveFeatureTab(document.getElementById('tabMyCourses'));
  } else if (viewName === 'roadmap') {
    document.getElementById('learningRoadmapView').classList.remove('d-none');
    setActiveFeatureTab(document.getElementById('tabLearningRoadmap'));
  } else if (viewName === 'lesson') {
    document.getElementById('lessonReaderView').classList.remove('d-none');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  } else if (viewName === 'admin') {
    document.getElementById('courseAdminView').classList.remove('d-none');
    setActiveFeatureTab(document.getElementById('tabCourseAdmin'));
  }
}

function hideAllLmsViews() {
  const views = ['courseCatalogView', 'myCoursesView', 'learningRoadmapView', 'lessonReaderView', 'courseAdminView'];
  views.forEach(id => {
    const el = document.getElementById(id);
    if (el) el.classList.add('d-none');
  });
}

function setActiveFeatureTab(tabButton) {
  if (!tabButton) return;
  document.querySelectorAll('#featureNavTabs .nav-link').forEach(btn => btn.classList.remove('active'));
  tabButton.classList.add('active');
}

// =========================================================================
// 2. COURSE CATALOG
// =========================================================================

function initLmsFilters() {
  const careerFilter = document.getElementById('filterCourseCareer');
  const difficultyFilter = document.getElementById('filterCourseDifficulty');
  const searchInput = document.getElementById('searchCourseInput');
  const searchBtn = document.getElementById('searchCourseBtn');

  if (careerFilter) {
    careerFilter.addEventListener('change', (e) => {
      currentFilterCareer = e.target.value;
      loadCourseCatalog();
    });
  }

  if (difficultyFilter) {
    difficultyFilter.addEventListener('change', (e) => {
      currentFilterDifficulty = e.target.value;
      loadCourseCatalog();
    });
  }

  if (searchInput) {
    searchInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        currentSearchQuery = searchInput.value.trim();
        loadCourseCatalog();
      }
    });
  }

  if (searchBtn) {
    searchBtn.addEventListener('click', () => {
      currentSearchQuery = searchInput ? searchInput.value.trim() : '';
      loadCourseCatalog();
    });
  }
}

async function loadCourseCatalog() {
  const grid = document.getElementById('coursesGrid');
  if (!grid) return;

  grid.innerHTML = `
    <div class="col-12 text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted small">Loading courses...</p>
    </div>
  `;

  try {
    const params = new URLSearchParams();
    if (currentFilterCareer && currentFilterCareer !== 'ALL') params.append('careerId', currentFilterCareer);
    if (currentFilterDifficulty && currentFilterDifficulty !== 'ALL') params.append('difficulty', currentFilterDifficulty);
    if (currentSearchQuery) params.append('query', currentSearchQuery);

    const token = getToken();
    const headers = token ? { 'Authorization': `Bearer ${token}` } : {};
    const res = await fetch(`/api/courses?${params.toString()}`, { credentials: 'omit', headers });
    if (!res.ok) throw new Error(`HTTP error ${res.status}`);

    const courses = await res.json();
    currentCourses = courses;
    renderCourseCatalog(courses);
  } catch (err) {
    console.error('Failed to load courses:', err);
    grid.innerHTML = `
      <div class="col-12 text-center py-5 text-danger">
        <i class="bi bi-exclamation-triangle-fill fs-2 mb-2 d-block"></i>
        <span>Unable to load course catalog. Please verify backend connection.</span>
      </div>
    `;
  }
}

function renderCourseCatalog(courses) {
  const grid = document.getElementById('coursesGrid');
  const countBadge = document.getElementById('catalogCountBadge');
  if (countBadge) countBadge.textContent = `${courses.length} Courses`;

  if (!courses || courses.length === 0) {
    grid.innerHTML = `
      <div class="col-12 text-center py-5">
        <div class="empty-icon-circle mx-auto mb-3">
          <i class="bi bi-journal-x fs-2 text-muted"></i>
        </div>
        <h4 class="h6 fw-bold">No courses matched your filters</h4>
        <p class="text-secondary small">Try adjusting your career track, difficulty, or search keyword.</p>
        <button class="btn btn-outline-primary btn-sm" onclick="resetFilters()">Reset Filters</button>
      </div>
    `;
    return;
  }

  grid.innerHTML = courses.map(course => {
    const badgeColor = course.badgeColor || 'primary';
    const isEnrolled = course.enrollmentStatus && course.enrollmentStatus !== 'NOT_ENROLLED';
    const progress = course.progressPercentage || 0;
    const resumeLessonId = course.lastAccessedLessonId || course.firstLessonId;

    let actionBtnHtml = '';
    if (isEnrolled) {
      if (progress >= 100) {
        actionBtnHtml = `
          <button class="btn btn-outline-success w-100 fw-semibold d-flex align-items-center justify-content-center" onclick="openLesson(${course.id}, ${resumeLessonId})">
            <i class="bi bi-check-circle-fill me-2"></i> Review Completed
          </button>
        `;
      } else {
        actionBtnHtml = `
          <button class="btn btn-primary w-100 fw-semibold d-flex align-items-center justify-content-center" onclick="openLesson(${course.id}, ${resumeLessonId})">
            <i class="bi bi-play-circle-fill me-2"></i> Continue Learning (${progress}%)
          </button>
        `;
      }
    } else {
      actionBtnHtml = `
        <button class="btn btn-outline-primary w-100 fw-semibold d-flex align-items-center justify-content-center" onclick="handleEnrollCourse(${course.id}, '${escapeHtml(course.slug)}')">
          <i class="bi bi-mortarboard-fill me-2"></i> Enroll in Course
        </button>
      `;
    }

    return `
      <div class="col-12 col-md-6 col-lg-4">
        <div class="course-card">
          <div class="d-flex justify-content-between align-items-start">
            <div class="course-icon-badge course-badge-${badgeColor}">
              <i class="bi ${course.icon || 'bi-journal-code'}"></i>
            </div>
            <span class="badge ${getDifficultyBadgeClass(course.difficulty)} px-2 py-1">
              ${escapeHtml(course.difficulty)}
            </span>
          </div>

          <h3 class="course-title">${escapeHtml(course.title)}</h3>
          <p class="course-desc">${escapeHtml(course.description)}</p>

          <div class="course-meta-pills">
            <span class="meta-pill"><i class="bi bi-briefcase me-1"></i>${escapeHtml(course.careerTitle)}</span>
            <span class="meta-pill"><i class="bi bi-bullseye me-1"></i>${escapeHtml(course.skillCategory)}</span>
            <span class="meta-pill"><i class="bi bi-clock me-1"></i>${escapeHtml(course.estimatedDuration)}</span>
            <span class="meta-pill"><i class="bi bi-book me-1"></i>${course.lessonCount} Lessons</span>
          </div>

          ${isEnrolled ? `
            <div class="course-progress-container">
              <div class="d-flex justify-content-between small text-muted mb-1">
                <span>Progress</span>
                <span class="fw-bold text-dark">${progress}%</span>
              </div>
              <div class="course-progress-bar">
                <div class="course-progress-fill" style="width: ${progress}%;"></div>
              </div>
            </div>
          ` : ''}

          <div class="mt-auto pt-2">
            ${actionBtnHtml}
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function getDifficultyBadgeClass(difficulty) {
  if (difficulty === 'BEGINNER') return 'bg-success-subtle text-success border border-success-subtle';
  if (difficulty === 'INTERMEDIATE') return 'bg-warning-subtle text-warning border border-warning-subtle';
  return 'bg-danger-subtle text-danger border border-danger-subtle';
}

function resetFilters() {
  currentFilterCareer = 'ALL';
  currentFilterDifficulty = 'ALL';
  currentSearchQuery = '';
  const c = document.getElementById('filterCourseCareer');
  const d = document.getElementById('filterCourseDifficulty');
  const s = document.getElementById('searchCourseInput');
  if (c) c.value = 'ALL';
  if (d) d.value = 'ALL';
  if (s) s.value = '';
  loadCourseCatalog();
}

async function handleEnrollCourse(courseId, slug) {
  if (!isLoggedIn()) {
    window.location.href = `/login.html?redirect=course&course=${slug}`;
    return;
  }

  try {
    const res = await authFetch(`/api/courses/${courseId}/enroll`, {
      method: 'POST'
    });
    if (!res.ok) throw new Error('Enrollment failed');
    const courseDto = await res.json();
    // Open first lesson immediately
    const lessonId = courseDto.firstLessonId || courseDto.lastAccessedLessonId;
    if (lessonId) {
      openLesson(courseId, lessonId);
    } else {
      loadCourseCatalog();
    }
  } catch (err) {
    alert('Failed to enroll in course: ' + err.message);
  }
}

// =========================================================================
// 3. LESSON READER & THEORY CONTENT
// =========================================================================

async function openLesson(courseId, lessonId) {
  if (!isLoggedIn()) {
    window.location.href = `/login.html?redirect=lesson&course=${courseId}&lesson=${lessonId}`;
    return;
  }

  switchLmsView('lesson');
  const container = document.getElementById('lessonReaderView');
  container.innerHTML = `
    <div class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted small">Loading lesson and theory content...</p>
    </div>
  `;

  try {
    // 1. Fetch lesson details
    const res = await authFetch(`/api/courses/${courseId}/lessons/${lessonId}`);
    if (!res.ok) throw new Error('Failed to load lesson');
    const lesson = await res.json();
    currentLessonData = lesson;

    // 2. Fetch course syllabus for sidebar
    const courseRes = await authFetch(`/api/courses/${courseId}`);
    const courseData = await courseRes.json();

    renderLessonReader(lesson, courseData);
  } catch (err) {
    console.error('Error loading lesson:', err);
    container.innerHTML = `
      <div class="alert alert-danger my-4 p-4 text-center">
        <i class="bi bi-exclamation-triangle-fill fs-2 mb-2 d-block"></i>
        <h5>Failed to load lesson</h5>
        <p class="small mb-3">${escapeHtml(err.message)}</p>
        <button class="btn btn-outline-danger btn-sm" onclick="switchLmsView('catalog')">Back to Courses</button>
      </div>
    `;
  }
}

function renderLessonReader(lesson, courseData) {
  const container = document.getElementById('lessonReaderView');
  const lessonsList = courseData.lessons || [];

  const syllabusHtml = lessonsList.map(l => {
    const isActive = l.id === lesson.id;
    const isCompleted = l.completed;
    return `
      <div class="lesson-nav-item ${isActive ? 'active' : ''}" onclick="openLesson(${lesson.courseId}, ${l.id})">
        <span class="lesson-nav-icon ${isCompleted ? 'bg-success text-white' : (isActive ? 'bg-primary text-white' : 'bg-light text-secondary border')}">
          ${isCompleted ? '<i class="bi bi-check-lg"></i>' : l.lessonOrder}
        </span>
        <span class="text-truncate flex-grow-1">${escapeHtml(l.title)}</span>
        <small class="text-muted">${l.estimatedMinutes}m</small>
      </div>
    `;
  }).join('');

  container.innerHTML = `
    <!-- Top Breadcrumbs & Progress -->
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
      <div>
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb mb-1 small">
            <li class="breadcrumb-item"><a href="javascript:void(0)" onclick="switchLmsView('catalog')">Courses</a></li>
            <li class="breadcrumb-item"><a href="javascript:void(0)" onclick="openCourseDetailsModal('${lesson.courseSlug}')">${escapeHtml(lesson.courseTitle)}</a></li>
            <li class="breadcrumb-item active" aria-current="page">Lesson ${lesson.lessonOrder}</li>
          </ol>
        </nav>
        <h1 class="h3 fw-bold mb-0 text-dark">${escapeHtml(lesson.title)}</h1>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-outline-secondary btn-sm" onclick="switchLmsView('catalog')">
          <i class="bi bi-arrow-left me-1"></i> Back to Catalog
        </button>
        ${lesson.completed ? `
          <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2">
            <i class="bi bi-check-circle-fill me-1"></i> Completed
          </span>
        ` : `
          <button class="btn btn-success btn-sm px-3" onclick="handleMarkLessonComplete(${lesson.id})">
            <i class="bi bi-check2-circle me-1"></i> Mark as Completed
          </button>
        `}
      </div>
    </div>

    <!-- Layout: Sidebar + Main Content -->
    <div class="lesson-layout">
      <!-- Left Sidebar Syllabus -->
      <aside class="lesson-sidebar">
        <h6 class="fw-bold text-uppercase small text-muted mb-3 d-flex justify-content-between">
          <span>Course Syllabus</span>
          <span>${courseData.progressPercentage}%</span>
        </h6>
        <div class="course-progress-bar mb-3">
          <div class="course-progress-fill" style="width: ${courseData.progressPercentage}%;"></div>
        </div>
        <div class="lesson-nav-list">
          ${syllabusHtml}
        </div>
      </aside>

      <!-- Main Reader Panel -->
      <main class="lesson-main-content">
        <!-- Learning Objectives -->
        ${lesson.learningObjectives ? `
          <div class="lesson-objectives-box">
            <h5 class="h6 fw-bold text-success mb-2 d-flex align-items-center">
              <i class="bi bi-bullseye me-2"></i> Learning Objectives
            </h5>
            <div class="small text-secondary mb-0">
              ${formatMarkdownText(lesson.learningObjectives)}
            </div>
          </div>
        ` : ''}

        <!-- Introduction -->
        ${lesson.introduction ? `
          <div class="lead mb-4" style="font-size: 1.05rem; line-height: 1.7; color: #334155;">
            ${escapeHtml(lesson.introduction)}
          </div>
        ` : ''}

        <!-- In-Depth Theory Content -->
        <article class="lesson-theory mb-4" style="line-height: 1.75; font-size: 1rem; color: #1e293b;">
          ${formatMarkdownContent(lesson.content)}
        </article>

        <!-- Code Examples -->
        ${lesson.codeExamples ? `
          <div class="my-4">
            <h4 class="h5 fw-bold mb-2 d-flex align-items-center text-dark">
              <i class="bi bi-code-slash text-primary me-2"></i> Code Implementation & Walkthrough
            </h4>
            <div class="lesson-code-block">
              <button class="code-copy-btn" onclick="copyCodeSnippet(this)">
                <i class="bi bi-clipboard me-1"></i> Copy
              </button>
              <pre class="mb-0"><code>${escapeHtml(lesson.codeExamples)}</code></pre>
            </div>
          </div>
        ` : ''}

        <!-- Important Concepts Callout -->
        ${lesson.importantConcepts ? `
          <div class="lesson-callout-box">
            <h5 class="h6 fw-bold text-warning mb-2 d-flex align-items-center">
              <i class="bi bi-lightbulb-fill me-2"></i> Critical Concepts & Best Practices
            </h5>
            <div class="small text-secondary mb-0">
              ${formatMarkdownText(lesson.importantConcepts)}
            </div>
          </div>
        ` : ''}

        <!-- Summary -->
        ${lesson.summary ? `
          <div class="lesson-summary-box">
            <h5 class="h6 fw-bold text-primary mb-2 d-flex align-items-center">
              <i class="bi bi-journal-check me-2"></i> Key Takeaways & Summary
            </h5>
            <div class="small text-secondary mb-0">
              ${formatMarkdownText(lesson.summary)}
            </div>
          </div>
        ` : ''}

        <!-- Practice Exercises -->
        ${lesson.practiceQuestions ? `
          <div class="p-3 rounded-3 bg-light border mb-4">
            <h5 class="h6 fw-bold text-dark mb-2 d-flex align-items-center">
              <i class="bi bi-pencil-square text-secondary me-2"></i> Practice Exercises & Self-Reflection
            </h5>
            <div class="small text-muted mb-0">
              ${formatMarkdownText(lesson.practiceQuestions)}
            </div>
          </div>
        ` : ''}

        <!-- End of Lesson Quiz Section -->
        <div id="lessonQuizContainer" class="quiz-container">
          <div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div> Loading lesson quiz...
        </div>

        <!-- Navigation Buttons -->
        <div class="d-flex justify-content-between align-items-center mt-5 pt-4 border-top">
          ${lesson.previousLessonId ? `
            <button class="btn btn-outline-secondary px-4 py-2 fw-semibold" onclick="openLesson(${lesson.courseId}, ${lesson.previousLessonId})">
              <i class="bi bi-arrow-left me-2"></i> Previous Lesson
            </button>
          ` : `<div></div>`}

          <div class="d-flex gap-2">
            <button class="btn btn-outline-primary px-3 py-2 fw-semibold" onclick="handleMarkLessonComplete(${lesson.id})">
              <i class="bi bi-check-circle me-1"></i> ${lesson.completed ? 'Completed' : 'Mark Complete'}
            </button>
            ${lesson.nextLessonId ? `
              <button class="btn btn-primary px-4 py-2 fw-semibold" onclick="openLesson(${lesson.courseId}, ${lesson.nextLessonId})">
                Next Lesson <i class="bi bi-arrow-right ms-2"></i>
              </button>
            ` : `
              <button class="btn btn-success px-4 py-2 fw-semibold" onclick="switchLmsView('catalog')">
                <i class="bi bi-trophy-fill me-2"></i> Finish Course
              </button>
            `}
          </div>
        </div>
      </main>
    </div>
  `;

  // Load quiz for this lesson
  loadLessonQuiz(lesson.id);
}

function formatMarkdownText(text) {
  if (!text) return '';
  return text.split('\n').map(line => {
    line = line.trim();
    if (!line) return '';
    if (line.startsWith('•') || line.startsWith('-')) {
      return `<p class="mb-1"><i class="bi bi-check2 text-success me-2"></i>${escapeHtml(line.replace(/^[•\-]\s*/, ''))}</p>`;
    }
    if (/^\d+\./.test(line)) {
      return `<p class="mb-1"><strong>${line.match(/^\d+\./)[0]}</strong> ${escapeHtml(line.replace(/^\d+\.\s*/, ''))}</p>`;
    }
    return `<p class="mb-2">${escapeHtml(line)}</p>`;
  }).join('');
}

function formatMarkdownContent(content) {
  if (!content) return '';
  const lines = content.split('\n');
  let html = '';
  let inList = false;

  for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    if (line.startsWith('### ')) {
      if (inList) { html += '</ul>'; inList = false; }
      html += `<h4 class="h5 fw-bold mt-4 mb-2 text-dark">${escapeHtml(line.substring(4))}</h4>`;
    } else if (line.startsWith('## ')) {
      if (inList) { html += '</ul>'; inList = false; }
      html += `<h3 class="h4 fw-bold mt-4 mb-3 text-dark">${escapeHtml(line.substring(3))}</h3>`;
    } else if (line.startsWith('- ') || line.startsWith('* ')) {
      if (!inList) { html += '<ul class="mb-3 ps-3">'; inList = true; }
      html += `<li class="mb-1">${escapeHtml(line.substring(2))}</li>`;
    } else if (line.trim() === '') {
      if (inList) { html += '</ul>'; inList = false; }
    } else {
      if (inList) { html += '</ul>'; inList = false; }
      html += `<p class="mb-3">${escapeHtml(line)}</p>`;
    }
  }
  if (inList) html += '</ul>';
  return html;
}

function copyCodeSnippet(button) {
  const pre = button.parentElement.querySelector('code');
  if (!pre) return;
  navigator.clipboard.writeText(pre.innerText).then(() => {
    button.innerHTML = '<i class="bi bi-check2 me-1"></i> Copied!';
    setTimeout(() => {
      button.innerHTML = '<i class="bi bi-clipboard me-1"></i> Copy';
    }, 2000);
  });
}

async function handleMarkLessonComplete(lessonId) {
  try {
    const res = await authFetch(`/api/courses/lessons/${lessonId}/complete`, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to update progress');
    const data = await res.json();
    alert(`Lesson marked as completed! Course progress: ${data.courseProgressPercentage}%`);
    openLesson(data.courseId, lessonId);
  } catch (err) {
    alert('Failed to mark lesson complete: ' + err.message);
  }
}

// =========================================================================
// 4. LESSON QUIZZES
// =========================================================================

async function loadLessonQuiz(lessonId) {
  const container = document.getElementById('lessonQuizContainer');
  if (!container) return;

  try {
    const res = await fetch(`/api/courses/lessons/${lessonId}/quiz`);
    if (!res.ok) throw new Error('Failed to fetch quiz');
    const questions = await res.json();

    if (!questions || questions.length === 0) {
      container.innerHTML = `
        <div class="text-muted small">No quiz configured for this lesson.</div>
      `;
      return;
    }

    renderQuizForm(lessonId, questions);
  } catch (err) {
    container.innerHTML = `<div class="text-muted small">Unable to load quiz at this time.</div>`;
  }
}

function renderQuizForm(lessonId, questions) {
  const container = document.getElementById('lessonQuizContainer');
  container.innerHTML = `
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="h5 fw-bold mb-1 text-dark d-flex align-items-center">
          <i class="bi bi-patch-question-fill text-primary me-2"></i> End-of-Lesson Knowledge Check
        </h4>
        <span class="text-secondary small">Test your comprehension with 4-choice multiple-choice questions. Score at least 70% to pass!</span>
      </div>
      <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2">
        ${questions.length} Questions
      </span>
    </div>

    <form id="lessonQuizForm" onsubmit="handleQuizSubmit(event, ${lessonId})">
      ${questions.map((q, idx) => `
        <div class="quiz-question-card" id="quizCard_${q.id}">
          <div class="fw-bold mb-3 text-dark">
            <span class="badge bg-secondary-subtle text-secondary me-2">Q${idx + 1}</span>
            ${escapeHtml(q.questionText)}
          </div>
          <div class="quiz-options-group" data-qid="${q.id}">
            ${q.options.map((opt, optIdx) => `
              <label class="quiz-option-label" onclick="selectQuizOption(this, ${q.id}, ${optIdx})">
                <input type="radio" name="q_${q.id}" value="${optIdx}" class="form-check-input me-2 d-none" required>
                <span class="badge bg-light text-dark border me-2">${String.fromCharCode(65 + optIdx)}</span>
                <span>${escapeHtml(opt)}</span>
              </label>
            `).join('')}
          </div>
          <div id="quizExplain_${q.id}" class="d-none mt-2"></div>
        </div>
      `).join('')}

      <div id="quizResultAlert" class="alert d-none mb-3"></div>

      <button type="submit" class="btn btn-primary fw-semibold px-4 py-2" id="submitQuizBtn">
        <i class="bi bi-send-check me-1"></i> Submit Quiz Answers
      </button>
    </form>
  `;
}

function selectQuizOption(labelEl, questionId, optionIndex) {
  const group = labelEl.closest('.quiz-options-group');
  group.querySelectorAll('.quiz-option-label').forEach(lbl => lbl.classList.remove('selected'));
  labelEl.classList.add('selected');
  const radio = labelEl.querySelector('input[type="radio"]');
  if (radio) radio.checked = true;
}

async function handleQuizSubmit(e, lessonId) {
  e.preventDefault();
  if (!isLoggedIn()) {
    alert('Please sign in to submit your quiz and record progress.');
    return;
  }

  const form = document.getElementById('lessonQuizForm');
  const formData = new FormData(form);
  const answers = {};

  const optionGroups = form.querySelectorAll('.quiz-options-group');
  for (let group of optionGroups) {
    const qid = group.getAttribute('data-qid');
    const checked = group.querySelector('input[type="radio"]:checked');
    if (!checked) {
      alert(`Please answer question Q${qid} before submitting.`);
      return;
    }
    answers[qid] = parseInt(checked.value, 10);
  }

  const submitBtn = document.getElementById('submitQuizBtn');
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Grading...';

  try {
    const res = await authFetch(`/api/courses/lessons/${lessonId}/quiz`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ answers })
    });

    if (!res.ok) throw new Error('Failed to evaluate quiz');
    const result = await res.json();
    currentQuizSubmission = result;
    displayQuizResults(result);
  } catch (err) {
    alert('Quiz submission failed: ' + err.message);
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<i class="bi bi-send-check me-1"></i> Submit Quiz Answers';
  }
}

function displayQuizResults(result) {
  const alertEl = document.getElementById('quizResultAlert');
  alertEl.classList.remove('d-none', 'alert-success', 'alert-warning');
  alertEl.classList.add(result.passed ? 'alert-success' : 'alert-warning');

  alertEl.innerHTML = `
    <div class="d-flex align-items-center">
      <i class="bi ${result.passed ? 'bi-trophy-fill text-success fs-3' : 'bi-exclamation-circle-fill text-warning fs-3'} me-3"></i>
      <div>
        <h5 class="h6 fw-bold mb-1">${result.passed ? 'Quiz Passed!' : 'Quiz Needs Review'} (Score: ${result.scorePercentage}%)</h5>
        <span class="small">${escapeHtml(result.feedbackMessage)}</span>
      </div>
    </div>
  `;

  // Render question explanations and color codes
  result.questionResults.forEach(qr => {
    const card = document.getElementById(`quizCard_${qr.questionId}`);
    if (!card) return;

    const labels = card.querySelectorAll('.quiz-option-label');
    labels.forEach((label, idx) => {
      label.classList.remove('selected', 'correct', 'incorrect');
      if (idx === qr.correctOption) {
        label.classList.add('correct');
      } else if (idx === qr.selectedOption && !qr.correct) {
        label.classList.add('incorrect');
      }
    });

    const expEl = document.getElementById(`quizExplain_${qr.questionId}`);
    if (expEl) {
      expEl.className = 'quiz-explanation-box';
      expEl.innerHTML = `
        <div class="d-flex align-items-start gap-2">
          <i class="bi ${qr.correct ? 'bi-check-circle-fill text-success' : 'bi-x-circle-fill text-danger'} mt-1"></i>
          <div>
            <strong>${qr.correct ? 'Correct!' : 'Incorrect.'}</strong> ${escapeHtml(qr.explanation)}
          </div>
        </div>
      `;
    }
  });

  const submitBtn = document.getElementById('submitQuizBtn');
  submitBtn.disabled = false;
  submitBtn.innerHTML = result.passed
    ? '<i class="bi bi-check-all me-1"></i> Passed (Score Saved)'
    : '<i class="bi bi-arrow-repeat me-1"></i> Retake Quiz';
}

// =========================================================================
// 5. MY COURSES VIEW
// =========================================================================

async function loadMyCourses() {
  const container = document.getElementById('myCoursesGrid');
  if (!container) return;

  container.innerHTML = `
    <div class="col-12 text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted small">Loading your enrolled courses...</p>
    </div>
  `;

  try {
    const res = await authFetch('/api/courses/my-enrollments');
    if (!res.ok) throw new Error('Failed to load enrollments');
    const enrollments = await res.json();
    renderMyCourses(enrollments);
  } catch (err) {
    console.error('Error loading my courses:', err);
    container.innerHTML = `
      <div class="col-12 text-center py-5 text-danger">
        <i class="bi bi-exclamation-triangle-fill fs-2 mb-2 d-block"></i>
        <span>Unable to load your enrolled courses.</span>
      </div>
    `;
  }
}

function renderMyCourses(courses) {
  const container = document.getElementById('myCoursesGrid');
  const countEl = document.getElementById('myCoursesCount');
  if (countEl) countEl.textContent = `${courses.length} Active Courses`;

  if (!courses || courses.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center py-5">
        <div class="empty-icon-circle mx-auto mb-3">
          <i class="bi bi-mortarboard fs-2 text-muted"></i>
        </div>
        <h4 class="h6 fw-bold">You haven't enrolled in any courses yet</h4>
        <p class="text-secondary small">Browse our course catalog or take a career assessment to receive personalized course recommendations.</p>
        <button class="btn btn-primary btn-sm" onclick="switchLmsView('catalog')">
          <i class="bi bi-search me-1"></i> Browse Course Catalog
        </button>
      </div>
    `;
    return;
  }

  container.innerHTML = courses.map(course => {
    const progress = course.progressPercentage || 0;
    const badgeColor = course.badgeColor || 'primary';
    const resumeLessonId = course.lastAccessedLessonId || course.firstLessonId;

    return `
      <div class="col-12 col-md-6 col-lg-4">
        <div class="course-card">
          <div class="d-flex justify-content-between align-items-start">
            <div class="course-icon-badge course-badge-${badgeColor}">
              <i class="bi ${course.icon || 'bi-journal-code'}"></i>
            </div>
            <span class="badge ${progress >= 100 ? 'bg-success text-white' : 'bg-primary text-white'} px-2 py-1">
              ${progress >= 100 ? 'Completed' : 'In Progress'}
            </span>
          </div>

          <h3 class="course-title">${escapeHtml(course.title)}</h3>
          <p class="course-desc">${escapeHtml(course.description)}</p>

          <div class="course-meta-pills">
            <span class="meta-pill"><i class="bi bi-briefcase me-1"></i>${escapeHtml(course.careerTitle)}</span>
            <span class="meta-pill"><i class="bi bi-clock me-1"></i>${escapeHtml(course.estimatedDuration)}</span>
          </div>

          <div class="course-progress-container">
            <div class="d-flex justify-content-between small text-muted mb-1">
              <span>Course Progress</span>
              <span class="fw-bold text-dark">${progress}%</span>
            </div>
            <div class="course-progress-bar">
              <div class="course-progress-fill" style="width: ${progress}%;"></div>
            </div>
          </div>

          <div class="mt-auto pt-2">
            <button class="btn btn-primary w-100 fw-semibold d-flex align-items-center justify-content-center" onclick="openLesson(${course.id}, ${resumeLessonId})">
              <i class="bi bi-play-circle-fill me-2"></i> ${progress >= 100 ? 'Review Course' : 'Resume Lesson'}
            </button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

// =========================================================================
// 6. PERSONALIZED LEARNING ROADMAP
// =========================================================================

async function loadLearningRoadmap() {
  const container = document.getElementById('roadmapContent');
  if (!container) return;

  container.innerHTML = `
    <div class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted small">Generating your personalized skill-gap roadmap...</p>
    </div>
  `;

  try {
    const res = await authFetch('/api/courses/roadmap');
    if (!res.ok) throw new Error('Failed to load roadmap');
    const roadmap = await res.json();
    renderLearningRoadmap(roadmap);
  } catch (err) {
    console.error('Error loading roadmap:', err);
    container.innerHTML = `
      <div class="alert alert-danger my-4 p-4 text-center">
        <i class="bi bi-exclamation-triangle-fill fs-2 mb-2 d-block"></i>
        <h5>Failed to generate learning roadmap</h5>
        <p class="small mb-3">${escapeHtml(err.message)}</p>
      </div>
    `;
  }
}

function renderLearningRoadmap(roadmap) {
  const container = document.getElementById('roadmapContent');

  if (!roadmap || !roadmap.recommendedCourses || roadmap.recommendedCourses.length === 0) {
    container.innerHTML = `
      <div class="text-center py-5">
        <div class="empty-icon-circle mx-auto mb-3">
          <i class="bi bi-signpost-split fs-2 text-muted"></i>
        </div>
        <h4 class="h5 fw-bold">No Assessment Evaluated Yet</h4>
        <p class="text-secondary small max-w-sm mx-auto mb-3">
          Take a Career-Based Assessment to benchmark your current skills and generate an adaptive learning roadmap.
        </p>
        <button class="btn btn-primary" onclick="switchToCareerAssessmentTab()">
          <i class="bi bi-compass-fill me-1"></i> Take Career Assessment Now
        </button>
      </div>
    `;
    return;
  }

  const prioritySkillsHtml = (roadmap.prioritySkills || []).map(s => `
    <div class="p-3 bg-white rounded-3 border mb-2 d-flex justify-content-between align-items-center">
      <div>
        <div class="d-flex align-items-center gap-2">
          <strong>${escapeHtml(s.skill)}</strong>
          <span class="roadmap-priority-badge roadmap-priority-${s.priority}">${s.priority} PRIORITY</span>
        </div>
        <small class="text-muted">Current: ${s.score}% (Target: ${s.target}%)</small>
      </div>
      <div class="text-end">
        <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1">
          -${s.gap} pts gap
        </span>
      </div>
    </div>
  `).join('');

  const coursesHtml = roadmap.recommendedCourses.map((rec, idx) => {
    const isEnrolled = rec.enrolled;
    const progress = rec.progressPercentage || 0;

    return `
      <div class="roadmap-step-card">
        <div class="d-flex justify-content-between align-items-start gap-3 mb-2">
          <div class="d-flex align-items-center gap-2">
            <span class="badge bg-primary text-white rounded-pill px-3 py-1">Step ${idx + 1}</span>
            <h4 class="h6 fw-bold mb-0 text-dark">${escapeHtml(rec.courseTitle)}</h4>
          </div>
          <span class="badge ${getDifficultyBadgeClass(rec.difficulty)}">${rec.difficulty}</span>
        </div>

        <!-- Personalized Reason Callout -->
        <div class="p-3 bg-light rounded-3 border mb-3 small text-secondary">
          <i class="bi bi-lightbulb-fill text-warning me-1"></i>
          <strong>Why this course?</strong> ${escapeHtml(rec.reason)}
        </div>

        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2">
          <div class="small text-muted">
            <span class="me-3"><i class="bi bi-clock me-1"></i>${escapeHtml(rec.duration)}</span>
            <span><i class="bi bi-bullseye me-1"></i>${escapeHtml(rec.skillCategory)}</span>
          </div>

          <div>
            ${isEnrolled ? `
              <button class="btn btn-outline-primary btn-sm fw-semibold" onclick="openLessonBySlug('${rec.courseSlug}')">
                <i class="bi bi-play-circle me-1"></i> Continue Course (${progress}%)
              </button>
            ` : `
              <button class="btn btn-primary btn-sm fw-semibold" onclick="handleEnrollCourse(${rec.courseId}, '${rec.courseSlug}')">
                <i class="bi bi-plus-circle me-1"></i> Enroll in Course
              </button>
            `}
          </div>
        </div>
      </div>
    `;
  }).join('');

  container.innerHTML = `
    <!-- Top Summary Banner -->
    <div class="hero rounded-4 p-4 mb-4 text-white">
      <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
          <span class="badge rounded-pill bg-white text-dark mb-2 px-3 py-1 fw-bold">
            <i class="bi bi-compass me-1 text-primary"></i> ${escapeHtml(roadmap.careerTitle)}
          </span>
          <h2 class="h3 fw-bold mb-1">Your Adaptive Learning Roadmap</h2>
          <p class="text-light small mb-0">Personalized sequence designed to close your ${roadmap.totalIdentifiedGaps} total skill gap points.</p>
        </div>
        <div class="text-center p-3 rounded-3 bg-white bg-opacity-10 border border-white border-opacity-25">
          <small class="d-block text-uppercase fw-semibold" style="font-size: 0.75rem;">CURRENT READINESS</small>
          <strong class="fs-2">${roadmap.readinessScore}%</strong>
        </div>
      </div>
    </div>

    <!-- Reassessment Warning & Guidance Disclaimer -->
    <div class="alert alert-info border-info-subtle d-flex align-items-start gap-3 p-4 rounded-4 mb-4" role="alert">
      <i class="bi bi-shield-check fs-2 text-primary flex-shrink-0"></i>
      <div class="flex-grow-1">
        <h5 class="h6 fw-bold text-dark mb-1">Skill Mastery Verification Principle</h5>
        <p class="small text-secondary mb-3">${escapeHtml(roadmap.reassessmentMessage)}</p>
        <button class="btn btn-primary btn-sm fw-semibold px-3" onclick="switchToCareerAssessmentTab()">
          <i class="bi bi-arrow-repeat me-1"></i> Retake ${escapeHtml(roadmap.careerTitle)} Assessment
        </button>
      </div>
    </div>

    <div class="row g-4">
      <!-- Left Column: Priority Skills Ranking -->
      <div class="col-12 col-lg-5">
        <div class="panel h-100">
          <h3 class="h5 fw-bold mb-3 d-flex align-items-center text-dark">
            <i class="bi bi-bar-chart-steps text-danger me-2"></i> Priority Skill Gaps
          </h3>
          <p class="text-secondary small mb-3">Skills identified with largest distance to industry benchmarks.</p>
          ${prioritySkillsHtml}
        </div>
      </div>

      <!-- Right Column: Step-by-Step Suggested Sequence -->
      <div class="col-12 col-lg-7">
        <div class="panel h-100">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="h5 fw-bold mb-0 text-dark">
              <i class="bi bi-diagram-3-fill text-success me-2"></i> Recommended Learning Sequence
            </h3>
            <span class="badge bg-secondary-subtle text-secondary">${roadmap.totalEstimatedTime} total</span>
          </div>
          <p class="text-secondary small mb-3">Courses prioritized to resolve largest gaps first.</p>
          ${coursesHtml}
        </div>
      </div>
    </div>
  `;
}

function switchToCareerAssessmentTab() {
  const tab = document.getElementById('tabCareerAssessment');
  if (tab) tab.click();
  window.scrollTo({ top: 300, behavior: 'smooth' });
}

function openLessonBySlug(slug) {
  const course = currentCourses.find(c => c.slug === slug);
  if (course) {
    const resumeLessonId = course.lastAccessedLessonId || course.firstLessonId;
    openLesson(course.id, resumeLessonId);
  } else {
    switchLmsView('catalog');
  }
}

// =========================================================================
// 7. ADMIN COURSE MANAGEMENT
// =========================================================================

async function loadAdminCourses() {
  const tbody = document.getElementById('adminCoursesTableBody');
  if (!tbody) return;

  tbody.innerHTML = `
    <tr>
      <td colspan="7" class="text-center py-4">
        <div class="spinner-border spinner-border-sm text-primary me-2"></div> Loading courses...
      </td>
    </tr>
  `;

  try {
    const res = await authFetch('/api/admin/courses');
    if (!res.ok) throw new Error('Failed to load admin courses');
    const courses = await res.json();
    renderAdminCoursesTable(courses);
  } catch (err) {
    tbody.innerHTML = `
      <tr>
        <td colspan="7" class="text-danger text-center py-4">
          Failed to load courses: ${escapeHtml(err.message)}
        </td>
      </tr>
    `;
  }
}

function renderAdminCoursesTable(courses) {
  const tbody = document.getElementById('adminCoursesTableBody');
  if (!courses || courses.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4">No courses configured.</td></tr>`;
    return;
  }

  tbody.innerHTML = courses.map(c => `
    <tr>
      <td class="fw-bold">${c.id}</td>
      <td>
        <strong>${escapeHtml(c.title)}</strong><br>
        <small class="text-muted">${escapeHtml(c.slug)}</small>
      </td>
      <td><span class="badge bg-light text-dark border">${escapeHtml(c.careerTitle)}</span></td>
      <td>${escapeHtml(c.skillCategory)}</td>
      <td><span class="badge ${getDifficultyBadgeClass(c.difficulty)}">${c.difficulty}</span></td>
      <td>
        <span class="badge ${c.published ? 'bg-success' : 'bg-secondary'}">
          ${c.published ? 'Published' : 'Draft'}
        </span>
      </td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-secondary me-1" onclick="handleTogglePublish(${c.id})">
          ${c.published ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>'}
        </button>
        <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditCourseModal(${c.id})">
          <i class="bi bi-pencil"></i>
        </button>
        <button class="btn btn-sm btn-outline-danger" onclick="handleDeleteCourse(${c.id})">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    </tr>
  `).join('');
}

async function handleTogglePublish(courseId) {
  try {
    const res = await authFetch(`/api/admin/courses/${courseId}/publish`, { method: 'PATCH' });
    if (!res.ok) throw new Error('Toggle failed');
    loadAdminCourses();
  } catch (err) {
    alert('Failed to toggle publish status: ' + err.message);
  }
}

async function handleDeleteCourse(courseId) {
  if (!confirm('Are you sure you want to delete this course and all its lessons?')) return;
  try {
    const res = await authFetch(`/api/admin/courses/${courseId}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Delete failed');
    loadAdminCourses();
  } catch (err) {
    alert('Failed to delete course: ' + err.message);
  }
}

function openCreateCourseModal() {
  document.getElementById('adminCourseModalTitle').textContent = 'Create New Course';
  document.getElementById('adminCourseForm').reset();
  document.getElementById('adminCourseId').value = '';
  const modal = new bootstrap.Modal(document.getElementById('adminCourseModal'));
  modal.show();
}

async function openEditCourseModal(courseId) {
  try {
    const res = await authFetch(`/api/courses/${courseId}`);
    if (!res.ok) throw new Error('Failed to load course');
    const data = await res.json();
    const c = data.course;

    document.getElementById('adminCourseModalTitle').textContent = 'Edit Course';
    document.getElementById('adminCourseId').value = c.id;
    document.getElementById('adminCourseSlug').value = c.slug;
    document.getElementById('adminCourseTitle').value = c.title;
    document.getElementById('adminCourseDesc').value = c.description;
    document.getElementById('adminCourseCareerId').value = c.careerId;
    document.getElementById('adminCourseCareerTitle').value = c.careerTitle;
    document.getElementById('adminCourseSkill').value = c.skillCategory;
    document.getElementById('adminCourseDifficulty').value = c.difficulty;
    document.getElementById('adminCourseDuration').value = c.estimatedDuration;
    document.getElementById('adminCourseIcon').value = c.icon;

    const modal = new bootstrap.Modal(document.getElementById('adminCourseModal'));
    modal.show();
  } catch (err) {
    alert('Failed to open edit modal: ' + err.message);
  }
}

async function handleSaveAdminCourse(e) {
  e.preventDefault();
  const id = document.getElementById('adminCourseId').value;
  const payload = {
    slug: document.getElementById('adminCourseSlug').value.trim(),
    title: document.getElementById('adminCourseTitle').value.trim(),
    description: document.getElementById('adminCourseDesc').value.trim(),
    careerId: document.getElementById('adminCourseCareerId').value.trim(),
    careerTitle: document.getElementById('adminCourseCareerTitle').value.trim(),
    skillCategory: document.getElementById('adminCourseSkill').value.trim(),
    difficulty: document.getElementById('adminCourseDifficulty').value,
    estimatedDuration: document.getElementById('adminCourseDuration').value.trim(),
    icon: document.getElementById('adminCourseIcon').value.trim(),
    badgeColor: 'primary',
    published: true
  };

  try {
    const url = id ? `/api/admin/courses/${id}` : '/api/admin/courses';
    const method = id ? 'PUT' : 'POST';
    const res = await authFetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'Save failed');
    }

    const modalEl = document.getElementById('adminCourseModal');
    const modal = bootstrap.Modal.getInstance(modalEl);
    if (modal) modal.hide();
    loadAdminCourses();
  } catch (err) {
    alert('Failed to save course: ' + err.message);
  }
}
