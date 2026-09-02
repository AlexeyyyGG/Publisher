document.addEventListener('DOMContentLoaded', function () {
  const publicationSelect = document.getElementById('publicationId');
  const coAuthorsSelect = document.getElementById('coAuthorsIds');
  publicationSelect.addEventListener('change', function () {
    const publicationId = this.value;
    coAuthorsSelect.innerHTML = '';
    if (!publicationId) {
      return;
    }
    const baseUrl = publicationSelect.getAttribute('data-url');
    const url = baseUrl + '?publicationId=' + encodeURIComponent(publicationId);
    fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error('Не удалось загрузить список соавторов');
      }
      return response.json();
    })
    .then(journalists => {
      journalists.forEach(journalist => {
        const option = document.createElement('option');
        option.value = journalist.id;
        option.textContent = journalist.shortName;
        coAuthorsSelect.appendChild(option);
      });
    })
    .catch(error => {
      console.error(error);
    });
  });
});