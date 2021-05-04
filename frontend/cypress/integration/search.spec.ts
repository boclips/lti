context("LTI search", () => {
  const endpoint = "http://localhost:9000/search.html";

  it("can filter search results", () => {

    cy.visit(`${endpoint}`);
    cy.wait(1000);
    cy.get('[data-qa="search-input"]').type("Minute");
    cy.get("button")
      .contains("Search")
      .click();
    cy.wait(1000);
    cy.get("[data-boclips-player-initialised=true]").should("be.visible");
    cy.get("[data-boclips-player-initialised=true] button").should(
      "be.visible"
    );

    cy.get('[data-qa="video-card-wrapper"]').should((videoCard) => {
      expect(videoCard.length).to.equal(2);
    });

    cy.get('[data-qa="video-card-wrapper"]').should((videoCard) => {
      expect(videoCard.length).to.equal(1);
    });
  });
});
